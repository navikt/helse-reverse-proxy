import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val dusseldorfKtorVersion = "1.3.2.6840711"
val mainClass = "no.nav.helse.AppKt"

plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"

}

buildscript {
    apply("https://raw.githubusercontent.com/navikt/dusseldorf-ktor/684071162815a8c33befcd0e2fab40a5b987ba2e/gradle/dusseldorf-ktor.gradle.kts")
}

dependencies {
    implementation ( "no.nav.helse:dusseldorf-ktor-core:$dusseldorfKtorVersion")
    implementation ( "no.nav.helse:dusseldorf-ktor-client:$dusseldorfKtorVersion")
}

repositories {
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://kotlin.bintray.com/kotlinx")

    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/navikt/dusseldorf-ktor")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }

    jcenter()
    mavenLocal()
    mavenCentral()
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("app")
    archiveClassifier.set("")
    manifest {
        attributes(
            mapOf(
                "Main-Class" to mainClass
            )
        )
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "6.4.1"
}
