package dev.protsenko.codeguard.rules.jpa

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.isObjectOrAnyType
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.core.notSuppressedClassesAndInterfaces
import dev.protsenko.codeguard.rules.SpringAnnotations
import dev.protsenko.codeguard.rules.isSpringDataRepository

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

                val failures = scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.entityAnnotations)
                    .mapNotNull { entity ->
                        val visitedClassNames = mutableSetOf<String>()
                        val hasIdField = generateSequence(entity) { current ->
                            current.parentClass?.name?.let { classesByName[it] }
                        }.takeWhile { visitedClassNames.add(it.name) }
                            .any { klass ->
                                klass.properties()
                                    .any { it.hasAnnotationWithName(SpringAnnotations.idAnnotations) }
                            }
                        if (!hasIdField) "@Entity class ${entity.name} must have a field annotated with @Id"
                        else null
                    }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
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
                val failures = scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.controllerAnnotations)
                    .mapNotNull { controller ->
                        if (controller.hasAnnotationWithName(SpringAnnotations.transactionalAnnotations)) {
                            "Controller ${controller.name} has @Transactional annotation. " +
                                "Transactions should be managed at the service layer."
                        } else {
                            val transactionalMethods = controller
                                .functions()
                                .withAnnotationNamed(SpringAnnotations.transactionalAnnotations)
                            if (transactionalMethods.isNotEmpty()) {
                                val methodNames = transactionalMethods.joinToString(", ") { it.name }
                                "Controller ${controller.name} has @Transactional methods: $methodNames. " +
                                    "Transactions should be managed at the service layer."
                            } else null
                        }
                    }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
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
                val failures = scope
                    .notSuppressedClasses(suppressKey)
                    .filter { it.resideInPackage("..domain..") || it.resideInPackage("..entity..") }
                    .mapNotNull { domainClass ->
                        val springAnnotations =
                            domainClass.annotations
                                .filter { it.fullyQualifiedName?.startsWith("org.springframework.") == true }
                        if (springAnnotations.isNotEmpty()) {
                            val annotationNames = springAnnotations.joinToString(", ") { it.name }
                            "Domain class ${domainClass.name} has Spring annotations: $annotationNames. " +
                                "Domain layer should be framework-independent."
                        } else {
                            val springImports = domainClass.containingFile.imports
                                .filter { it.name.startsWith("org.springframework.") }
                            if (springImports.isNotEmpty()) {
                                val importNames = springImports.joinToString(", ") { it.name }
                                "Domain class ${domainClass.name} imports Spring classes: $importNames. " +
                                    "Domain layer should be framework-independent."
                            } else null
                        }
                    }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
            }
        }

    /**
     * Rule: Spring Data repositories should not return raw Object/Any.
     */
    val repositoryReturnTypeRule =
        object : SpringBootRule {
            override val description = "Repositories should not return Object or Any"
            override val suppressKey = "CodeGuard:repositoryReturnType"

            override fun verify(scope: KoScope) {
                val failures =
                    scope
                        .notSuppressedClassesAndInterfaces(suppressKey)
                        .filter { it.isSpringDataRepository() }
                        .flatMap { it.functions() }
                        .filter { it.hasPublicOrDefaultModifier }
                        .mapNotNull { function ->
                            function.returnType?.name?.takeIf { isObjectOrAnyType(it) }?.let { typeName ->
                                "Repository method ${function.containingDeclaration}.${function.name} " +
                                    "returns $typeName. Use a concrete return type instead."
                            }
                        }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
            }
        }
}

val allJpaRules: List<SpringBootRule> = listOf(
    JpaRules.entityIdRule,
    JpaRules.noDataClassEntityRule,
    JpaRules.transactionalPlacementRule,
    JpaRules.domainLayerIndependenceRule,
    JpaRules.repositoryReturnTypeRule,
)
