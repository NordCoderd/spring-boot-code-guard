package dev.protsenko.codeguard.rules

/**
 * Fully-qualified annotation names and composite groups shared across all rule implementations.
 */
object SpringAnnotations {
    // ---- Stereotypes ----
    const val COMPONENT = "org.springframework.stereotype.Component"
    const val SERVICE = "org.springframework.stereotype.Service"
    const val REPOSITORY = "org.springframework.stereotype.Repository"
    const val CONTROLLER = "org.springframework.stereotype.Controller"
    const val REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController"

    // ---- Configuration ----
    const val CONFIGURATION = "org.springframework.context.annotation.Configuration"
    const val BEAN = "org.springframework.context.annotation.Bean"
    const val AUTOWIRED = "org.springframework.beans.factory.annotation.Autowired"
    const val VALUE = "org.springframework.beans.factory.annotation.Value"
    const val CONFIGURATION_PROPERTIES = "org.springframework.boot.context.properties.ConfigurationProperties"

    // ---- Web MVC ----
    const val REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping"
    const val GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping"
    const val POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping"
    const val PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping"
    const val DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping"
    const val PATCH_MAPPING = "org.springframework.web.bind.annotation.PatchMapping"
    const val REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody"
    const val REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam"
    const val REST_CONTROLLER_ADVICE = "org.springframework.web.bind.annotation.RestControllerAdvice"
    const val CONTROLLER_ADVICE = "org.springframework.web.bind.annotation.ControllerAdvice"

    // ---- Validation ----
    const val VALID_JAKARTA = "jakarta.validation.Valid"
    const val VALID_JAVAX = "javax.validation.Valid"
    const val VALIDATED = "org.springframework.validation.annotation.Validated"

    // ---- JPA ----
    const val ENTITY_JAKARTA = "jakarta.persistence.Entity"
    const val ENTITY_JAVAX = "javax.persistence.Entity"
    const val ID_JAKARTA = "jakarta.persistence.Id"
    const val ID_JAVAX = "javax.persistence.Id"

    // ---- Transactions ----
    const val TRANSACTIONAL = "org.springframework.transaction.annotation.Transactional"
    const val TRANSACTIONAL_JAKARTA = "jakarta.transaction.Transactional"
    const val TRANSACTIONAL_JAVAX = "javax.transaction.Transactional"

    // ---- Injection ----
    const val INJECT_JAVAX = "javax.inject.Inject"
    const val INJECT_JAKARTA = "jakarta.inject.Inject"

    // ---- Composite groups ----

    /** @Controller and @RestController. */
    val controllerAnnotations = listOf(CONTROLLER, REST_CONTROLLER)

    /** @Entity (jakarta + javax). */
    val entityAnnotations = listOf(ENTITY_JAKARTA, ENTITY_JAVAX)

    /** @Id (jakarta + javax). */
    val idAnnotations = listOf(ID_JAKARTA, ID_JAVAX)

    /** All @Transactional variants. */
    val transactionalAnnotations = listOf(TRANSACTIONAL, TRANSACTIONAL_JAKARTA, TRANSACTIONAL_JAVAX)

    /** All injection annotations (@Autowired, @Inject). */
    val injectionAnnotations = listOf(AUTOWIRED, INJECT_JAVAX, INJECT_JAKARTA)

    /** HTTP method mapping annotations. */
    val httpMappingAnnotations = listOf(
        REQUEST_MAPPING, GET_MAPPING, POST_MAPPING, PUT_MAPPING, DELETE_MAPPING, PATCH_MAPPING,
    )

    /** @Valid and @Validated (both jakarta + javax + Spring). */
    val validationAnnotations = listOf(VALID_JAKARTA, VALID_JAVAX, VALIDATED)

    /**
     * @ConfigurationProperties — includes the simple name as fallback for fixtures that write
     * the annotation as a fully-qualified inline reference without an import statement.
     */
    val configurationPropertiesAnnotations = listOf(CONFIGURATION_PROPERTIES, "ConfigurationProperties")

    /** All Spring bean/component stereotype annotations. */
    val springBeanAnnotations = listOf(COMPONENT, SERVICE, REPOSITORY, CONTROLLER, REST_CONTROLLER)
}
