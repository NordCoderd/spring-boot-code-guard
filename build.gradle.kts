plugins {
    kotlin("jvm") version "2.3.0"
    `java-library`
    id("org.jetbrains.kotlinx.kover") version "0.9.7"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("com.vanniktech.maven.publish") version "0.31.0"
}

group = "dev.protsenko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Konsist needs to be in api/implementation scope as our rules use its types
    api("com.lemonappdev:konsist:0.17.3")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

detekt {
    config.setFrom(files("detekt.yml"))
}

kover {
    reports {
        verify {
            rule {
                // NB: Do not override this value
                minBound(90)
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("dev.protsenko", "spring-boot-code-guard", "1.0.0")

    pom {
        name = "Spring Boot Code Guard"
        description = "Set of Spring Boot Best Practices converted into Konsist tests."
        url = "https://github.com/NordCoderd/spring-boot-code-guard"
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }
        developers {
            developer {
                id = "NordCoderd"
                name = "Dmitry Protsenko"
                email = "tech@protsenko.dev"
            }
        }
        scm {
            url = "https://github.com/NordCoderd/spring-boot-code-guard"
            connection = "scm:git:git://github.com/NordCoderd/spring-boot-code-guard.git"
            developerConnection = "scm:git:ssh://git@github.com/NordCoderd/spring-boot-code-guard.git"
        }
    }
}

tasks.register("codeBaseline") {
    dependsOn("clean", "test", "detekt", "koverVerify")
    description = "Runs tests, Detekt, and Kover verification"
    group = "verification"
}
