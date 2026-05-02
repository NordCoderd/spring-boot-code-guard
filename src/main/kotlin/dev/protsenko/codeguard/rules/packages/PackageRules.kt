package dev.protsenko.codeguard.rules.packages

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import com.lemonappdev.konsist.api.ext.list.withoutAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.core.notSuppressedClassesAndInterfaces
import dev.protsenko.codeguard.rules.SpringAnnotations
import dev.protsenko.codeguard.rules.isSpringDataRepository

/**
 * Rules for package structure and naming conventions.
 */
object PackageRules {
    private val configurationPropertiesPrefixRegex =
        Regex("""ConfigurationProperties\s*\(\s*(?:prefix\s*=\s*)?"([^"]+)"""")
    private val kebabCasePrefixRegex =
        Regex("""^[a-z0-9]+(?:-[a-z0-9]+)*(?:\.[a-z0-9]+(?:-[a-z0-9]+)*)*$""")

    private fun configurationPropertiesPrefix(annotationText: String): String? =
        configurationPropertiesPrefixRegex
            .find(annotationText)
            ?.groupValues
            ?.getOrNull(1)

    private fun idClassReference(argumentValue: String?): String? =
        argumentValue
            ?.substringBefore("::class")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

    private fun resolvedIdClassReferences(
        reference: String,
        packageName: String?,
        importedTypes: List<String>,
    ): Set<String> =
        when {
            reference.contains(".") -> {
                setOf(reference)
            }

            else -> {
                buildSet {
                    packageName?.let { add("$it.$reference") }
                    importedTypes
                        .filter { it.substringAfterLast(".") == reference }
                        .forEach(::add)
                }
            }
        }

    /**
     * Rule: Package names should follow lowercase convention.
     * Note: package-level suppression is not applicable here.
     */
    val packageNamingRule =
        object : SpringBootRule {
            override val description = "Package names should be lowercase"
            override val suppressKey = "CodeGuard:packageNaming"

            override fun verify(scope: KoScope) {
                scope
                    .packages
                    .filter { pkg -> pkg.name.any { it.isUpperCase() } }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingPackages = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Package names should be lowercase: $violatingPackages",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: @Service classes should reside in .service package.
     */
    val servicePackageRule =
        object : SpringBootRule {
            override val description = "@Service classes should be in .service package"
            override val suppressKey = "CodeGuard:servicePackage"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.SERVICE)
                    .filterNot { it.resideInPackage("..service..") }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses =
                                violations.joinToString(", ") {
                                    "${it.name} (${it.packagee?.name ?: "no package"})"
                                }
                            throw AssertionError(
                                "@Service classes should be in .service package: $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: Controllers should reside in .controller or .web package.
     */
    val controllerPackageRule =
        object : SpringBootRule {
            override val description = "Controllers should be in .controller or .web package"
            override val suppressKey = "CodeGuard:controllerPackage"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.controllerAnnotations)
                    .filterNot {
                        it.resideInPackage("..controller..") || it.resideInPackage("..web..")
                    }.also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses =
                                violations.joinToString(", ") {
                                    "${it.name} (${it.packagee?.name ?: "no package"})"
                                }
                            throw AssertionError(
                                "Controllers should be in .controller or .web package: $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: @Configuration classes should be in .config or .configuration package.
     */
    val configurationPackageRule =
        object : SpringBootRule {
            override val description = "@Configuration classes should be in .config or .configuration package"
            override val suppressKey = "CodeGuard:configurationPackage"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.CONFIGURATION)
                    .withoutAnnotationNamed(SpringAnnotations.CONFIGURATION_PROPERTIES)
                    .filterNot {
                        it.resideInPackage("..config..") || it.resideInPackage("..configuration..")
                    }.also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses =
                                violations.joinToString(", ") {
                                    "${it.name} (${it.packagee?.name ?: "no package"})"
                                }
                            throw AssertionError(
                                "@Configuration classes should be in .config or .configuration package: $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: @ConfigurationProperties classes should be in .property package.
     * This promotes better organization of configuration properties classes.
     */
    val propertiesValidationRule =
        object : SpringBootRule {
            override val description = "@ConfigurationProperties classes should be in .property package"
            override val suppressKey = "CodeGuard:propertiesValidation"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.configurationPropertiesAnnotations)
                    .filterNot {
                        it.resideInPackage("..property..")
                    }.also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses =
                                violations.joinToString(", ") {
                                    "${it.name} (${it.packagee?.name ?: "no package"})"
                                }
                            throw AssertionError(
                                "@ConfigurationProperties classes should be in .property package: $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: @ConfigurationProperties prefixes should use lowercase kebab-case.
     */
    val configurationPropertiesPrefixKebabCaseRule =
        object : SpringBootRule {
            override val description = "@ConfigurationProperties prefixes should use lowercase kebab-case"
            override val suppressKey = "CodeGuard:configurationPropertiesPrefixKebabCase"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.configurationPropertiesAnnotations)
                    .forEach { klass ->
                        val prefix =
                            klass.annotations
                                .firstNotNullOfOrNull { annotation ->
                                    configurationPropertiesPrefix(annotation.text)
                                } ?: return@forEach

                        if (!kebabCasePrefixRegex.matches(prefix)) {
                            throw AssertionError(
                                "@ConfigurationProperties prefix should use lowercase kebab-case segments: " +
                                    "${klass.name} has prefix '$prefix'",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: only @Service classes (or their file-level helpers) should reside in .service package.
     */
    val onlyServicesInServicePackageRule =
        object : SpringBootRule {
            override val description = "Only @Service classes should be in .service package"
            override val suppressKey = "CodeGuard:onlyServicesInServicePackage"

            override fun verify(scope: KoScope) {
                val violations =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .filter { it.resideInPackage("..service..") }
                        .groupBy { it.containingFile }
                        .flatMap { (containingFile, classesInFile) ->
                            val topLevelClasses =
                                classesInFile.filter {
                                    it.containingDeclaration.toString() == containingFile.toString()
                                }

                            if (topLevelClasses.any { it.hasAnnotationWithName(SpringAnnotations.SERVICE) }) {
                                emptyList()
                            } else {
                                topLevelClasses
                            }
                        }

                if (violations.isNotEmpty()) {
                    val violatingClasses =
                        violations.joinToString(", ") {
                            "${it.name} (${it.packagee?.name ?: "no package"})"
                        }
                    throw AssertionError(
                        "Only @Service classes should be in .service package: $violatingClasses",
                    )
                }
            }
        }

    /**
     * Rule: @Repository classes/interfaces and Spring Data repository interfaces should reside in .repository package.
     */
    val repositoryPackageRule =
        object : SpringBootRule {
            override val description = "Repository classes should be in .repository package"
            override val suppressKey = "CodeGuard:repositoryPackage"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClassesAndInterfaces(suppressKey)
                    .filter { it.isSpringDataRepository() }
                    .filterNot { it.resideInPackage("..repository..") }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses =
                                violations.joinToString(", ") {
                                    "${it.name} (${it.packagee?.name ?: "no package"})"
                                }
                            throw AssertionError(
                                "Repository classes should be in .repository package: $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: only @Repository classes/interfaces (or their file-level helpers) should reside in .repository package.
     * Spring Data repository interfaces without explicit @Repository are also permitted.
     */
    val onlyRepositoriesInRepositoryPackageRule =
        object : SpringBootRule {
            override val description = "Only @Repository classes should be in .repository package"
            override val suppressKey = "CodeGuard:onlyRepositoriesInRepositoryPackage"

            override fun verify(scope: KoScope) {
                val violations =
                    scope
                        .notSuppressedClassesAndInterfaces(suppressKey)
                        .filter { it.resideInPackage("..repository..") }
                        .groupBy { it.containingFile }
                        .filterValues { declarationsInFile ->
                            declarationsInFile.none { it.isSpringDataRepository() }
                        }.values
                        .flatten()

                if (violations.isNotEmpty()) {
                    val violatingClasses =
                        violations.joinToString(", ") {
                            "${it.name} (${it.packagee?.name ?: "no package"})"
                        }
                    throw AssertionError(
                        "Only @Repository classes should be in .repository package: $violatingClasses",
                    )
                }
            }
        }

    /**
     * Rule: only @ConfigurationProperties classes (or their file-level helpers) should reside in .property package.
     */
    val onlyPropertiesInPropertyPackageRule =
        object : SpringBootRule {
            override val description = "Only @ConfigurationProperties classes should be in .property package"
            override val suppressKey = "CodeGuard:onlyPropertiesInPropertyPackage"

            override fun verify(scope: KoScope) {
                val violations =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .filter { it.resideInPackage("..property..") }
                        .groupBy { it.containingFile }
                        .filterValues { classesInFile ->
                            classesInFile.none { cls ->
                                SpringAnnotations.configurationPropertiesAnnotations.any {
                                    cls.hasAnnotationWithName(it)
                                }
                            }
                        }.values
                        .flatten()

                if (violations.isNotEmpty()) {
                    val violatingClasses =
                        violations.joinToString(", ") {
                            "${it.name} (${it.packagee?.name ?: "no package"})"
                        }
                    throw AssertionError(
                        "Only @ConfigurationProperties classes should be in .property package: $violatingClasses",
                    )
                }
            }
        }

    /**
     * Rule: only @Configuration, @ControllerAdvice, and @RestControllerAdvice classes
     * (or their file-level helpers) should reside in .config or .configuration package.
     */
    val onlyConfigurationsInConfigPackageRule =
        object : SpringBootRule {
            override val description =
                "Only @Configuration, @ControllerAdvice, or @RestControllerAdvice classes should be in .config or .configuration package"
            override val suppressKey = "CodeGuard:onlyConfigurationsInConfigPackage"

            override fun verify(scope: KoScope) {
                val violations =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .filter { it.resideInPackage("..config..") || it.resideInPackage("..configuration..") }
                        .groupBy { it.containingFile }
                        .filterValues { classesInFile ->
                            classesInFile.none { klass ->
                                SpringAnnotations.configurationPackageAnnotations.any { annotationName ->
                                    klass.hasAnnotationWithName(annotationName)
                                }
                            }
                        }.values
                        .flatten()

                if (violations.isNotEmpty()) {
                    val violatingClasses =
                        violations.joinToString(", ") {
                            "${it.name} (${it.packagee?.name ?: "no package"})"
                        }
                    throw AssertionError(
                        "Only @Configuration, @ControllerAdvice, or @RestControllerAdvice classes should be in .config or .configuration package: $violatingClasses",
                    )
                }
            }
        }

    /**
     * Rule: only @Controller/@RestController classes (or their file-level helpers) should reside in .controller or .web package.
     */
    val onlyControllersInControllerPackageRule =
        object : SpringBootRule {
            override val description = "Only @Controller classes should be in .controller or .web package"
            override val suppressKey = "CodeGuard:onlyControllersInControllerPackage"

            override fun verify(scope: KoScope) {
                val violations =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .filter { it.resideInPackage("..controller..") || it.resideInPackage("..web..") }
                        .groupBy { it.containingFile }
                        .filterValues { classesInFile ->
                            classesInFile.none {
                                it.hasAnnotationWithName(SpringAnnotations.CONTROLLER) ||
                                    it.hasAnnotationWithName(SpringAnnotations.REST_CONTROLLER)
                            }
                        }.values
                        .flatten()

                if (violations.isNotEmpty()) {
                    val violatingClasses =
                        violations.joinToString(", ") {
                            "${it.name} (${it.packagee?.name ?: "no package"})"
                        }
                    throw AssertionError(
                        "Only @Controller classes should be in .controller or .web package: $violatingClasses",
                    )
                }
            }
        }

    /**
     * Rule: only @Entity classes, classes inheriting from an @Entity, or their file-level helpers
     * should reside in .domain or .entity package.
     */
    val onlyEntitiesInEntityPackageRule =
        object : SpringBootRule {
            override val description = "Only @Entity classes should be in .domain or .entity package"
            override val suppressKey = "CodeGuard:onlyEntitiesInEntityPackage"

            override fun verify(scope: KoScope) {
                val referencedIdClassTypes =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .filter { klass ->
                            SpringAnnotations.entityAnnotations.any { annotationName ->
                                klass.hasAnnotationWithName(annotationName)
                            }
                        }.flatMap { klass ->
                            val importedTypes = klass.containingFile.imports.map { it.name }

                            klass.annotations
                                .filter { annotation ->
                                    annotation.fullyQualifiedName in SpringAnnotations.idClassAnnotations ||
                                        annotation.name == "IdClass"
                                }.flatMap { annotation ->
                                    annotation.arguments
                                        .mapNotNull { argument ->
                                            idClassReference(argument.value)
                                        }.flatMap { reference ->
                                            resolvedIdClassReferences(reference, klass.packagee?.name, importedTypes)
                                        }
                                }
                        }.toSet()

                val violations =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .filter { it.resideInPackage("..domain..") || it.resideInPackage("..entity..") }
                        .groupBy { it.containingFile }
                        .flatMap { (_, classesInFile) ->
                            val hasEntityAnchorInFile =
                                classesInFile.any { klass ->
                                    SpringAnnotations.entityAnnotations.any { annotationName ->
                                        klass.hasAnnotationWithName(annotationName)
                                    } ||
                                        klass.hasParentClass(indirectParents = true) { parent ->
                                            SpringAnnotations.entityAnnotations.any { annotationName ->
                                                parent.sourceDeclaration
                                                    ?.asClassDeclaration()
                                                    ?.hasAnnotationWithName(annotationName) == true
                                            }
                                        }
                                }

                            classesInFile.filterNot { klass ->
                                val inheritsFromEntity =
                                    klass.hasParentClass(indirectParents = true) { parent ->
                                        SpringAnnotations.entityAnnotations.any { annotationName ->
                                            parent.sourceDeclaration
                                                ?.asClassDeclaration()
                                                ?.hasAnnotationWithName(annotationName) == true
                                        }
                                    }
                                val classReference =
                                    klass.fullyQualifiedName ?: klass.packagee?.name?.let { "$it.${klass.name}" }
                                val isReferencedIdClass =
                                    classReference?.let { it in referencedIdClassTypes } == true

                                SpringAnnotations.entityPackageAnnotations.any { annotationName ->
                                    klass.hasAnnotationWithName(annotationName)
                                } ||
                                    inheritsFromEntity ||
                                    isReferencedIdClass ||
                                    hasEntityAnchorInFile
                            }
                        }

                if (violations.isNotEmpty()) {
                    val violatingClasses =
                        violations.joinToString(", ") {
                            "${it.name} (${it.packagee?.name ?: "no package"})"
                        }
                    throw AssertionError(
                        "Only @Entity classes should be in .domain or .entity package: $violatingClasses",
                    )
                }
            }
        }

    /**
     * Rule: @Entity classes should be in .domain or .entity package.
     */
    val entityPackageRule =
        object : SpringBootRule {
            override val description = "@Entity classes should be in .domain or .entity package"
            override val suppressKey = "CodeGuard:entityPackage"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.entityAnnotations)
                    .filterNot {
                        it.resideInPackage("..domain..") || it.resideInPackage("..entity..")
                    }.also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses =
                                violations.joinToString(", ") {
                                    "${it.name} (${it.packagee?.name ?: "no package"})"
                                }
                            throw AssertionError(
                                "@Entity classes should be in .domain or .entity package: $violatingClasses",
                            )
                        }
                    }
            }
        }
}

val allPackageRules: List<SpringBootRule> =
    listOf(
        PackageRules.packageNamingRule,
        PackageRules.servicePackageRule,
        PackageRules.controllerPackageRule,
        PackageRules.configurationPackageRule,
        PackageRules.propertiesValidationRule,
        PackageRules.configurationPropertiesPrefixKebabCaseRule,
        PackageRules.entityPackageRule,
        PackageRules.onlyServicesInServicePackageRule,
        PackageRules.onlyEntitiesInEntityPackageRule,
        PackageRules.onlyControllersInControllerPackageRule,
        PackageRules.onlyConfigurationsInConfigPackageRule,
        PackageRules.onlyPropertiesInPropertyPackageRule,
        PackageRules.repositoryPackageRule,
        PackageRules.onlyRepositoriesInRepositoryPackageRule,
    )
