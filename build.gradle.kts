import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

plugins {
    kotlin("jvm") version "2.3.0"
    `java-library`
    id("org.jetbrains.kotlinx.kover") version "0.9.7"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("com.vanniktech.maven.publish") version "0.36.0"
    id("org.sonarqube") version "7.3.0.8198"
}

group = "dev.protsenko"
version = "1.0.11"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.konsist)
    implementation(libs.kotlin.compiler.embeddable)

    testImplementation(kotlin("test"))
    testImplementation(libs.spring.boot.starter.data.jpa)
    testImplementation(libs.spring.boot.starter.web)
    testImplementation(libs.hibernate.validator)
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
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
    coordinates("dev.protsenko", "spring-boot-code-guard", version.toString())

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
    configureBasedOnAppliedPlugins(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = SourcesJar.Sources(),
    )
}

// Workaround: Gradle auto-injects mavenCentralUsername/mavenCentralPassword credentials into
// any repository named "mavenCentral", including the local file:// staging directory created by
// the vanniktech plugin. Gradle rejects authentication on file:// URLs, so we clear it first.
tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst {
        if (repository.url.scheme == "file") {
            repository.authentication.clear()
        }
    }
}

detekt {
    config.setFrom(rootProject.file("detekt.yml"))
    buildUponDefaultConfig = true
    source.setFrom("src/main/kotlin")
}

sonar {
    properties {
        property("sonar.projectKey", "NordCoderd_spring-boot-code-guard")
        property("sonar.organization", "nordcoderd")
        property("sonar.qualitygate.wait", "true")
        property("sonar.qualitygate.timeout", "300")
        property("sonar.coverage.exclusions", "*.gradle.kts,build.gradle.kts")
    }
}

val sonarProjectKey = "NordCoderd_spring-boot-code-guard"
val sonarHost = "https://sonarcloud.io"

tasks.register("sonarReport") {
    description = "Fetch Quality Gate status and open issues from SonarCloud."
    group = "verification"
    doLast {
        val token = System.getenv("SONAR_TOKEN")
        if (token.isNullOrBlank()) {
            logger.warn("SONAR_TOKEN not set; skipping issue fetch")
            return@doLast
        }
        val auth = Base64.getEncoder()
            .encodeToString("$token:".toByteArray())
        val client = HttpClient.newHttpClient()

        fun get(url: String): String {
            val req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic $auth")
                .GET()
                .build()
            return client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        }

        logger.lifecycle("\n=== Quality Gate ===")
        logger.lifecycle(get("$sonarHost/api/qualitygates/project_status?projectKey=$sonarProjectKey"))

        logger.lifecycle("\n=== Issues (open) ===")
        var page = 1
        val pageSize = 500
        val regex = Regex(
            "\"rule\":\"([^\"]+)\".*?\"severity\":\"([^\"]+)\".*?" +
                "\"component\":\"([^\"]+)\"(?:.*?\"line\":(\\d+))?.*?\"message\":\"([^\"]+)\""
        )
        while (true) {
            val body = get(
                "$sonarHost/api/issues/search" +
                    "?componentKeys=$sonarProjectKey&resolved=false&ps=$pageSize&p=$page"
            )
            val matches = regex.findAll(body).toList()
            if (matches.isEmpty()) break
            matches.forEach { m ->
                val (rule, sev, comp, line, msg) = m.destructured
                logger.lifecycle("[$sev] $comp:${line.ifBlank { "?" }} $rule — $msg")
            }
            if (matches.size < pageSize) break
            page++
            if (page * pageSize > 10_000) break
        }
    }
}

tasks.named("sonar") {
    finalizedBy("sonarReport")
}

tasks.register("codeBaseline") {
    dependsOn("clean", "test", "detektMain", "koverVerify", "koverXmlReport")
    description = "Runs tests, Detekt, and Kover verification"
    group = "verification"
}
