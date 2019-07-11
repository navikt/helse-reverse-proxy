import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val dusseldorfKtorVersion = "1.2.2.8f413ad"
val mainClass = "no.nav.helse.AppKt"

plugins {
    kotlin("jvm") version "1.3.40"
    id("com.github.johnrengelman.shadow") version "5.1.0"

}

buildscript {
    apply("https://raw.githubusercontent.com/navikt/dusseldorf-ktor/8f413ad909a79e6f5e5897f43f009152ab2f0f35/gradle/dusseldorf-ktor.gradle.kts")
}

dependencies {
    compile ( "no.nav.helse:dusseldorf-ktor-core:$dusseldorfKtorVersion")
    compile ( "no.nav.helse:dusseldorf-ktor-client:$dusseldorfKtorVersion")
}

repositories {
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("http://packages.confluent.io/maven/")

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
    gradleVersion = "5.5"
}
