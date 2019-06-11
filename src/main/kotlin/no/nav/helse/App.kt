package no.nav.helse

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.coroutines.awaitByteArrayResponseResult
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import io.ktor.application.*
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.callIdMdc
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Routing
import no.nav.helse.dusseldorf.ktor.core.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.net.URI
import java.util.*

private val logger: Logger = LoggerFactory.getLogger("nav.App")
private const val navCallIdHeader = "Nav-Call-Id"
private val monitoringPaths = listOf("isalive", "isready")

fun main(args: Array<String>): Unit  = io.ktor.server.netty.EngineMain.main(args)

fun Application.helseReverseProxy() {
    val mappings = Environment().getMappings()

    install(Routing) {
        DefaultProbeRoutes()
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        call.request.log()
    }

    install(CallId) {
        retrieve { call ->
            call.request.header(HttpHeaders.XCorrelationId) ?: call.request.header(navCallIdHeader)
        }
    }

    install(CallLogging) {
        callIdMdc("correlation_id")
        mdc("request_id") {"generated-${UUID.randomUUID()}"}
        logRequests()
    }

    intercept(ApplicationCallPipeline.Call) {
        if (!call.request.isMonitoringRequest()) {
            if (!call.request.hasValidPath()) {
                call.respondErrorAndLog(HttpStatusCode.BadGateway, "Invalid requested path.")
            } else if (!call.request.isMonitoringRequest())  {
                val destinationApplication = call.request.firstPathSegment()
                logger.trace("destinationApplication = '$destinationApplication'")
                val destinationPath = call.request.pathWithoutFirstPathSegment()
                logger.trace("destinationPath = '$destinationPath'")
                val httpMethod = call.request.httpMethod
                logger.trace("httpMethod = '$httpMethod'")
                if (!mappings.containsKey(destinationApplication)) {
                    call.respondErrorAndLog(HttpStatusCode.BadGateway, "Application '$destinationApplication' not configured.")
                } else  {
                    val parameters = call.request.queryParameters.toFuel()
                    val headers = call.request.headers.toFuel()
                    val body = call.receiveOrNull<ByteArray>()

                    val destinationUrl = produceDestinationUrl(destinationPath,
                        mappings.getValue(destinationApplication)
                    )

                    logger.trace("destinationUrl = '$destinationUrl'")

                    val httpRequest = initializeRequest(
                        httpMethod = httpMethod,
                        url = destinationUrl,
                        parameters = parameters
                    )
                        .header(headers)
                        .timeout(20_000)
                        .timeoutRead(20_000)

                    if (body != null) {
                        httpRequest.body( {ByteArrayInputStream(body) })
                    }

                    val (_, response, result) = httpRequest.awaitByteArrayResponseResult()
                    result.fold(
                        { success -> call.forward(response, success)},
                        { failure ->
                            if (-1 == response.statusCode) {
                                logger.error(failure.toString())
                                call.respondErrorAndLog(HttpStatusCode.GatewayTimeout, "Unable to proxy request.")
                            } else {
                                call.forward(response, failure.errorData)
                            }
                        }
                    )
                }
            }
        }
    }

}

private suspend fun ApplicationCall.forward(
    clientResponse: Response,
    body: ByteArray
) {
    clientResponse.headers.forEach { key, value ->
        if (!HttpHeaders.isUnsafe(key)) {
            value.forEach { response.header(key, it) }
        }
    }
    respondBytes(
        bytes = body,
        status = HttpStatusCode.fromValue(clientResponse.statusCode),
        contentType = clientResponse.contentType()
    )
}

private fun Response.contentType(): ContentType {
    val clientContentTypesHeaders = header(HttpHeaders.ContentType)
    return if (clientContentTypesHeaders.isEmpty()) ContentType.Text.Plain else ContentType.parse(clientContentTypesHeaders.first())
}

private fun Headers.toFuel(): Map<String, Any> {
    val fuelHeaders = mutableMapOf<String, Any>()
    forEach { key, values ->
        fuelHeaders[key] = values
    }
    return fuelHeaders.toMap()
}
private fun Parameters.toFuel(): List<Pair<String, Any?>> {
    val fuelParameters = mutableListOf<Pair<String, Any?>>()
    forEach { key, value ->
        value.forEach { fuelParameters.add(key to it) }
    }
    return fuelParameters.toList()
}
private fun initializeRequest(
    httpMethod: HttpMethod,
    url: URI,
    parameters: List<Pair<String, Any?>>
) : Request {
    return when (httpMethod.value.toLowerCase()) {
        "get" -> url.toString().httpGet(parameters)
        "post" -> url.toString().httpPost(parameters)
        "put" -> url.toString().httpPut(parameters)
        "delete" -> url.toString().httpDelete(parameters)
        else -> throw IllegalStateException("Ikke supportert HttpMethod $httpMethod")
    }
}


private suspend fun ApplicationCall.respondErrorAndLog(status: HttpStatusCode, error: String) {
    logger.error("HTTP $status -> $error")
    respond(status, error)
}

private fun ApplicationRequest.hasValidPath(): Boolean {
    val path = getPathWithoutLeadingSlashes()
    return path.isNotBlank()
}

private fun ApplicationRequest.getPathWithoutLeadingSlashes(): String {
    var path = path()
    while (path.startsWith("/")) {
        path = path.substring(1)
    }
    return path.toLowerCase()
}

private fun ApplicationRequest.firstPathSegment(): String {
    val path = getPathWithoutLeadingSlashes()
    return path.split("/")[0]
}

private fun ApplicationRequest.pathWithoutFirstPathSegment(): String {
    val path = getPathWithoutLeadingSlashes()
    val firstPathSegment = firstPathSegment()
    return path.substring(firstPathSegment.length)
}

private fun produceDestinationUrl(destinationPath: String, baseUrl: URI) : URI {
    return URLBuilder(baseUrl.toString())
        .trimmedPath(listOf(baseUrl.path, destinationPath))
        .build().toURI()
}

private fun URLBuilder.trimmedPath(pathParts : List<String>): URLBuilder  {
    val trimmedPathParts = mutableListOf<String>()
    pathParts.forEach { part ->
        if (part.isNotBlank()) {
            trimmedPathParts.add(part.trimStart('/').trimEnd('/'))
        }
    }
    return path(trimmedPathParts)
}

private fun ApplicationRequest.isMonitoringRequest() : Boolean {
    return monitoringPaths.contains(firstPathSegment())
}