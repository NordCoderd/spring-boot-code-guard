plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.springframework:spring-context:7.0.6")
    implementation("org.springframework:spring-tx:7.0.6")

    testImplementation(kotlin("test"))
    testImplementation("dev.protsenko:spring-boot-code-guard:1.0.9")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
