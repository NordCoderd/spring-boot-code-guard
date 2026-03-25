package jakarta.validation

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Valid
