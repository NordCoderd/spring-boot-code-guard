package dev.protsenko.codeguard.rules.jpa

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules for JPA entities and transaction management.
 */
object JpaRules {
    /**
     * Rule: @Entity classes must have @Id field.
     */
    val entityIdRule =
        object : SpringBootRule {
            override val description = "@Entity classes must have @Id field"
            override val suppressKey = "CodeGuard:entityId"

            override fun verify(scope: KoScope) {
                // Uses all classes (not notSuppressedClasses) intentionally for the parent-lookup map
                // so that suppressed base entities remain discoverable for @Id inheritance resolution.
                val classesByName = scope.classes().associateBy { it.name }

                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.entityAnnotations)
                    .forEach { entity ->
                        var current = entity
                        val visitedClassNames = mutableSetOf<String>()
                        var hasIdField = false

                        while (visitedClassNames.add(current.name)) {
                            val hasIdInCurrentClass =
                                current
                                    .properties()
                                    .any { it.hasAnnotationWithName(SpringAnnotations.idAnnotations) }
                            if (hasIdInCurrentClass) {
                                hasIdField = true
                                break
                            }

                            val parentName = current.parentClass?.name ?: break
                            current = classesByName[parentName] ?: break
                        }

                        if (!hasIdField) {
                            throw AssertionError(
                                "@Entity class ${entity.name} must have a field annotated with @Id",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: @Entity classes should not be data classes.
     * Data classes are incompatible with JPA entities due to:
     * - Final by default (prevents lazy loading proxies)
     * - Structural equality in equals/hashCode (breaks entity identity)
     * - Immutability assumptions (conflicts with JPA state management)
     */
    val noDataClassEntityRule =
        object : SpringBootRule {
            override val description = "@Entity classes should not be data classes"
            override val suppressKey = "CodeGuard:noDataClassEntity"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.entityAnnotations)
                    .filter { it.hasModifier(KoModifier.DATA) }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Data classes should not be used as @Entity: $violatingClasses. " +
                                    "Data classes are incompatible with JPA due to their final nature, " +
                                    "structural equality, and immutability assumptions.",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: @Transactional should be on the service layer, not controllers.
     */
    val transactionalPlacementRule =
        object : SpringBootRule {
            override val description = "@Transactional should be on service layer, not controllers"
            override val suppressKey = "CodeGuard:transactionalPlacement"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.controllerAnnotations)
                    .forEach { controller ->
                        // Check class-level @Transactional
                        if (controller.hasAnnotationWithName(SpringAnnotations.transactionalAnnotations)) {
                            throw AssertionError(
                                "Controller ${controller.name} has @Transactional annotation. " +
                                    "Transactions should be managed at the service layer.",
                            )
                        }

                        // Check method-level @Transactional
                        controller
                            .functions()
                            .withAnnotationNamed(SpringAnnotations.transactionalAnnotations)
                            .also { transactionalMethods ->
                                if (transactionalMethods.isNotEmpty()) {
                                    val methodNames = transactionalMethods.joinToString(", ") { it.name }
                                    throw AssertionError(
                                        "Controller ${controller.name} has @Transactional methods: $methodNames. " +
                                            "Transactions should be managed at the service layer.",
                                    )
                                }
                            }
                    }
            }
        }

    /**
     * Rule: Domain layer should have no Spring Framework dependencies.
     * Ensures clean architecture where domain is framework-independent.
     */
    val domainLayerIndependenceRule =
        object : SpringBootRule {
            override val description = "Domain layer should not depend on Spring Framework"
            override val suppressKey = "CodeGuard:domainLayerIndependence"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .filter { it.resideInPackage("..domain..") || it.resideInPackage("..entity..") }
                    .forEach { domainClass ->
                        // Check for Spring annotations
                        val springAnnotations =
                            domainClass.annotations
                                .filter { it.fullyQualifiedName?.startsWith("org.springframework.") == true }

                        if (springAnnotations.isNotEmpty()) {
                            val annotationNames = springAnnotations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Domain class ${domainClass.name} has Spring annotations: $annotationNames. " +
                                    "Domain layer should be framework-independent.",
                            )
                        }

                        // Check imports
                        domainClass.containingFile.imports
                            .filter { it.name.startsWith("org.springframework.") }
                            .also { springImports ->
                                if (springImports.isNotEmpty()) {
                                    val importNames = springImports.joinToString(", ") { it.name }
                                    throw AssertionError(
                                        "Domain class ${domainClass.name} imports Spring classes: $importNames. " +
                                            "Domain layer should be framework-independent.",
                                    )
                                }
                            }
                    }
            }
        }
}

val allJpaRules: List<SpringBootRule> = listOf(
    JpaRules.entityIdRule,
    JpaRules.noDataClassEntityRule,
    JpaRules.transactionalPlacementRule,
    JpaRules.domainLayerIndependenceRule,
)
