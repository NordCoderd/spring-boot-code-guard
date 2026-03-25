package org.springframework.boot.context.properties

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigurationProperties(
    val value: String = "",
)
