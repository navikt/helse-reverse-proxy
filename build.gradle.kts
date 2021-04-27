import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val dusseldorfKtorVersion = "1.5.2.fa18872"
val mainClass = "no.nav.helse.AppKt"

plugins {
    kotlin("jvm") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"

}

buildscript {
    apply("https://raw.githubusercontent.com/navikt/dusseldorf-ktor/fa1887241239fdd1877f0f258a94872281db8783/gradle/dusseldorf-ktor.gradle.kts")
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
    gradleVersion = "6.7.1"
}
