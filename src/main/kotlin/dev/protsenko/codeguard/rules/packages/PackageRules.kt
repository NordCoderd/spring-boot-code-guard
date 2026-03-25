package dev.protsenko.codeguard.rules.packages

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import com.lemonappdev.konsist.api.ext.list.withoutAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.rules.SpringAnnotations

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
                                "@ConfigurationProperties classes should be in .properties package: $violatingClasses",
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
    )
