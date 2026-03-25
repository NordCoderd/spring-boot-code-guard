package org.springframework.transaction.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Transactional(
    val readOnly: Boolean = false,
)
