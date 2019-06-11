package no.nav.helse

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

private val logger: Logger = LoggerFactory.getLogger("nav.Environment")

private const val ENVIRONMENT_VARIABLE_PREFIX = "HRP_"
private const val PREFIX_LENGTH = ENVIRONMENT_VARIABLE_PREFIX.length

data class Environment(val environmentVariables: Map<String, String> = System.getenv()) {

    private val mappings : Map<String, URI>

    init {
        val parsedMappings = mutableMapOf<String, URI>()

        environmentVariables.forEach { (key, value) ->
            if (key.startsWith(ENVIRONMENT_VARIABLE_PREFIX, ignoreCase = true)) {
                val contextPath = key.substring(PREFIX_LENGTH).toLowerCase().replace("_","-")
                val url = URI(value)
                parsedMappings[contextPath] = url
                logger.info("Added mapping '$contextPath' -> '$url'")
            }
        }

        mappings = parsedMappings.toMap()
    }

    fun getMappings() : Map<String, URI> {
        return mappings
    }
}