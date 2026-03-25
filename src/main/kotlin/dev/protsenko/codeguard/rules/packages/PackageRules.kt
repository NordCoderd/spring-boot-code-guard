package dev.protsenko.codeguard.rules.packages

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules for package structure and naming conventions.
 */
object PackageRules {
    /**
     * Rule: Package names should follow lowercase convention.
     */
    val packageNamingRule =
        object : SpringBootRule {
            override val description = "Package names should be lowercase"

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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
                    .withAnnotationNamed(SpringAnnotations.CONFIGURATION)
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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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
     * Rule: @Entity classes should be in .domain or .entity package.
     */
    val entityPackageRule =
        object : SpringBootRule {
            override val description = "@Entity classes should be in .domain or .entity package"

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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

val allPackageRules: List<SpringBootRule> = listOf(
    PackageRules.packageNamingRule,
    PackageRules.servicePackageRule,
    PackageRules.controllerPackageRule,
    PackageRules.configurationPackageRule,
    PackageRules.propertiesValidationRule,
    PackageRules.entityPackageRule,
)
