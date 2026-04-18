package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.packages.PackageRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for package domain rules.
 */
class PackageRulesViolationTest {
    @Test
    fun `packageNamingRule detects uppercase in package names`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/package/PackageNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.packageNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Package names should be lowercase: fixtures.violations.architecture.BadPackage",
            error.message,
        )
    }

    @Test
    fun `packageNamingRule passes for lowercase package names`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/package/PackageNamingPositive.kt"),
            )
        PackageRules.packageNamingRule.verify(positiveScope)
    }

    @Test
    fun `servicePackageRule detects Service not in service package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/ServicePackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.servicePackageRule.verify(negativeScope)
            }
        assertEquals(
            "@Service classes should be in .service package: ServiceNotInServicePackage (fixtures.violations.core.springcore)",
            error.message,
        )
    }

    @Test
    fun `servicePackageRule passes for Service in service package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/ServicePackagePositive.kt"),
            )
        PackageRules.servicePackageRule.verify(positiveScope)
    }

    @Test
    fun `controllerPackageRule detects Controller not in controller or web package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/ControllerPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.controllerPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Controllers should be in .controller or .web package: NotInControllerPackageController (fixtures.violations)",
            error.message,
        )
    }

    @Test
    fun `controllerPackageRule passes for Controller in controller package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/ControllerPackagePositive.kt"),
            )
        PackageRules.controllerPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyServicesInServicePackageRule detects @Component in service package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/service/ComponentInServicePackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyServicesInServicePackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Service classes should be in .service package: ComponentInServicePackage (fixtures.violations.packages.service)",
            error.message,
        )
    }

    @Test
    fun `onlyServicesInServicePackageRule detects @Repository in service package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/service/RepositoryInServicePackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyServicesInServicePackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Service classes should be in .service package: RepositoryInServicePackage (fixtures.violations.packages.service)",
            error.message,
        )
    }

    @Test
    fun `onlyServicesInServicePackageRule detects plain class alone in service package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/service/PlainClassInServicePackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyServicesInServicePackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Service classes should be in .service package: PlainClassInServicePackage (fixtures.violations.packages.service)",
            error.message,
        )
    }

    @Test
    fun `onlyServicesInServicePackageRule passes for @Service alone in service package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/service/ServiceAlonePositive.kt"),
            )
        PackageRules.onlyServicesInServicePackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyServicesInServicePackageRule passes for @Service with helper classes in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/service/ServiceWithHelperPositive.kt"),
            )
        PackageRules.onlyServicesInServicePackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyServicesInServicePackageRule passes for @Service with interface in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/service/ServiceWithInterfacePositive.kt"),
            )
        PackageRules.onlyServicesInServicePackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyServicesInServicePackageRule ignores nested helper classes in violating file`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/service/ComponentWithNestedHelperInServicePackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyServicesInServicePackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Service classes should be in .service package: ComponentWithNestedHelperInServicePackage (fixtures.violations.packages.service)",
            error.message,
        )
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule detects @Component in entity package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/ComponentInEntityPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyEntitiesInEntityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Entity classes should be in .domain or .entity package: ComponentInEntityPackage (fixtures.violations.packages.entity)",
            error.message,
        )
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule detects plain class alone in entity package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/PlainClassInEntityPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyEntitiesInEntityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Entity classes should be in .domain or .entity package: PlainClassInEntityPackage (fixtures.violations.packages.entity)",
            error.message,
        )
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for @Entity alone in entity package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/EntityAlonePositive.kt"),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for @Entity with helper classes in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/EntityWithHelperPositive.kt"),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for class extending @Entity in separate file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/packages/entity/BaseEntityForInheritancePositive.kt",
                    "src/test/kotlin/fixtures/violations/packages/entity/DerivedFromEntityInSeparateFilePositive.kt",
                ),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for class extending @Entity with helper in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/packages/entity/BaseEntityForInheritancePositive.kt",
                    "src/test/kotlin/fixtures/violations/packages/entity/DerivedFromEntityWithHelperPositive.kt",
                ),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for @MappedSuperclass in entity package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/MappedSuperclassInEntityPackagePositive.kt"),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for @Embeddable in entity package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/EmbeddableInEntityPackagePositive.kt"),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule detects plain helper next to @MappedSuperclass`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/MappedSuperclassWithPlainHelperInEntityPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyEntitiesInEntityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Entity classes should be in .domain or .entity package: PlainHelperNextToMappedSuperclass (fixtures.violations.packages.entity)",
            error.message,
        )
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule detects plain helper next to @Embeddable`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/entity/EmbeddableWithPlainHelperInEntityPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyEntitiesInEntityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Entity classes should be in .domain or .entity package: PlainHelperNextToEmbeddable (fixtures.violations.packages.entity)",
            error.message,
        )
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for @IdClass referenced type in separate file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/packages/entity/FindingGroupIdentifierPositive.kt",
                    "src/test/kotlin/fixtures/violations/packages/entity/YouTrackIssueEntityPositive.kt",
                ),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule detects plain helper next to @IdClass referenced type`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/packages/entity/FindingGroupIdentifierWithPlainHelperNegative.kt",
                    "src/test/kotlin/fixtures/violations/packages/entity/YouTrackIssueEntityWithPlainHelperPositive.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyEntitiesInEntityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Entity classes should be in .domain or .entity package: PlainHelperNextToIdClass (fixtures.violations.packages.entity)",
            error.message,
        )
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule does not allow unrelated class with same simple name as @IdClass type`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/packages/entity/FindingGroupIdentifierPositive.kt",
                    "src/test/kotlin/fixtures/violations/packages/entity/YouTrackIssueEntityPositive.kt",
                    "src/test/kotlin/fixtures/violations/packages/domain/FindingGroupIdentifierInDomainPackageNegative.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyEntitiesInEntityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Entity classes should be in .domain or .entity package: FindingGroupIdentifier (fixtures.violations.packages.domain)",
            error.message,
        )
    }

    @Test
    fun `onlyControllersInControllerPackageRule detects @Component in controller package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/controller/ComponentInControllerPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyControllersInControllerPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Controller classes should be in .controller or .web package: ComponentInControllerPackage (fixtures.violations.packages.controller)",
            error.message,
        )
    }

    @Test
    fun `onlyControllersInControllerPackageRule detects plain class alone in controller package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/controller/PlainClassInControllerPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyControllersInControllerPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Controller classes should be in .controller or .web package: PlainClassInControllerPackage (fixtures.violations.packages.controller)",
            error.message,
        )
    }

    @Test
    fun `onlyControllersInControllerPackageRule passes for @RestController alone in controller package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/controller/ControllerAlonePositive.kt"),
            )
        PackageRules.onlyControllersInControllerPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyControllersInControllerPackageRule passes for @RestController with helper classes in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/controller/ControllerWithHelperPositive.kt"),
            )
        PackageRules.onlyControllersInControllerPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule detects @Component in config package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/ComponentInConfigPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyConfigurationsInConfigPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Configuration, @ControllerAdvice, or @RestControllerAdvice classes should be in .config or .configuration package: ComponentInConfigPackage (fixtures.violations.packages.config)",
            error.message,
        )
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule detects plain class alone in config package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/PlainClassInConfigPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyConfigurationsInConfigPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Configuration, @ControllerAdvice, or @RestControllerAdvice classes should be in .config or .configuration package: PlainClassInConfigPackage (fixtures.violations.packages.config)",
            error.message,
        )
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule passes for @Configuration alone in config package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/ConfigurationAlonePositive.kt"),
            )
        PackageRules.onlyConfigurationsInConfigPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule passes for @Configuration with helper classes in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/ConfigurationWithHelperPositive.kt"),
            )
        PackageRules.onlyConfigurationsInConfigPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule passes for @ControllerAdvice alone in config package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/ControllerAdviceInConfigPackagePositive.kt"),
            )
        PackageRules.onlyConfigurationsInConfigPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule passes for @RestControllerAdvice alone in config package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/RestControllerAdviceInConfigPackagePositive.kt"),
            )
        PackageRules.onlyConfigurationsInConfigPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule passes for @RestControllerAdvice with helper classes in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/RestControllerAdviceWithHelperPositive.kt"),
            )
        PackageRules.onlyConfigurationsInConfigPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyPropertiesInPropertyPackageRule detects @Component in property package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/property/ComponentInPropertyPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyPropertiesInPropertyPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @ConfigurationProperties classes should be in .property package: ComponentInPropertyPackage (fixtures.violations.packages.property)",
            error.message,
        )
    }

    @Test
    fun `onlyPropertiesInPropertyPackageRule detects plain class alone in property package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/property/PlainClassInPropertyPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyPropertiesInPropertyPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @ConfigurationProperties classes should be in .property package: PlainClassInPropertyPackage (fixtures.violations.packages.property)",
            error.message,
        )
    }

    @Test
    fun `onlyPropertiesInPropertyPackageRule passes for @ConfigurationProperties alone in property package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/property/PropertiesAlonePositive.kt"),
            )
        PackageRules.onlyPropertiesInPropertyPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyPropertiesInPropertyPackageRule passes for @ConfigurationProperties with helper classes in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/property/PropertiesWithHelperPositive.kt"),
            )
        PackageRules.onlyPropertiesInPropertyPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyRepositoriesInRepositoryPackageRule detects @Component in repository package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/ComponentInRepositoryPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyRepositoriesInRepositoryPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Repository classes should be in .repository package: ComponentInRepositoryPackage (fixtures.violations.packages.repository)",
            error.message,
        )
    }

    @Test
    fun `onlyRepositoriesInRepositoryPackageRule detects plain class alone in repository package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/PlainClassInRepositoryPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyRepositoriesInRepositoryPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Repository classes should be in .repository package: PlainClassInRepositoryPackage (fixtures.violations.packages.repository)",
            error.message,
        )
    }

    @Test
    fun `onlyRepositoriesInRepositoryPackageRule passes for @Repository alone in repository package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/RepositoryAlonePositive.kt"),
            )
        PackageRules.onlyRepositoriesInRepositoryPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyRepositoriesInRepositoryPackageRule passes for @Repository with helper classes in same file`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/RepositoryWithHelperPositive.kt"),
            )
        PackageRules.onlyRepositoriesInRepositoryPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyRepositoriesInRepositoryPackageRule passes for Spring Data JpaRepository interface without explicit @Repository`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/JpaRepositoryPositive.kt"),
            )
        PackageRules.onlyRepositoriesInRepositoryPackageRule.verify(positiveScope)
    }

    @Test
    fun `repositoryPackageRule detects @Repository interface not in repository package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/repository/RepositoryPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.repositoryPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Repository classes should be in .repository package: RepositoryNotInRepoPackage (fixtures.violations.jpa)",
            error.message,
        )
    }

    @Test
    fun `repositoryPackageRule detects Spring Data JpaRepository interface not in repository package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/JpaRepositoryPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.repositoryPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Repository classes should be in .repository package: JpaRepositoryNotInRepoPackage (fixtures.violations.packages)",
            error.message,
        )
    }

    @Test
    fun `repositoryPackageRule passes for Spring Data interface in repository package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/repository/RepositoryPackagePositive.kt"),
            )
        PackageRules.repositoryPackageRule.verify(positiveScope)
    }

    @Test
    fun `repositoryPackageRule detects @Repository class not in repository package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/repository/RepositoryClassPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.repositoryPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Repository classes should be in .repository package: RepositoryClassNotInRepoPackage (fixtures.violations.jpa)",
            error.message,
        )
    }

    @Test
    fun `repositoryPackageRule passes for @Repository class in repository package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/RepositoryClassPositive.kt"),
            )
        PackageRules.repositoryPackageRule.verify(positiveScope)
    }

    @Test
    fun `repositoryPackageRule passes for @Repository interface in repository package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/RepositoryInterfacePositive.kt"),
            )
        PackageRules.repositoryPackageRule.verify(positiveScope)
    }

    @Test
    fun `repositoryPackageRule passes for Spring Data JpaRepository interface in repository package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/JpaRepositoryPositive.kt"),
            )
        PackageRules.repositoryPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyRepositoriesInRepositoryPackageRule detects plain interface without Spring Data parent in repository package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/repository/ApiClientInRepositoryPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyRepositoriesInRepositoryPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Repository classes should be in .repository package: ApiClient (fixtures.violations.packages.repository)",
            error.message,
        )
    }

    @Test
    fun `onlyControllersInControllerPackageRule detects @Component in web package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/web/ComponentInWebPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyControllersInControllerPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Controller classes should be in .controller or .web package: ComponentInWebPackage (fixtures.violations.packages.web)",
            error.message,
        )
    }

    @Test
    fun `onlyControllersInControllerPackageRule passes for @RestController alone in web package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/web/ControllerInWebPackagePositive.kt"),
            )
        PackageRules.onlyControllersInControllerPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule detects @Component in domain package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/domain/ComponentInDomainPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyEntitiesInEntityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Entity classes should be in .domain or .entity package: ComponentInDomainPackage (fixtures.violations.packages.domain)",
            error.message,
        )
    }

    @Test
    fun `onlyEntitiesInEntityPackageRule passes for @Entity alone in domain package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/domain/EntityInDomainPackagePositive.kt"),
            )
        PackageRules.onlyEntitiesInEntityPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule detects @Component in configuration package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/configuration/ComponentInConfigurationPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyConfigurationsInConfigPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Configuration, @ControllerAdvice, or @RestControllerAdvice classes should be in .config or .configuration package: ComponentInConfigurationPackage (fixtures.violations.packages.configuration)",
            error.message,
        )
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule passes for @Configuration alone in configuration package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/configuration/ConfigurationInConfigurationPackagePositive.kt"),
            )
        PackageRules.onlyConfigurationsInConfigPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule passes for @RestControllerAdvice in configuration package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/configuration/RestControllerAdviceInConfigurationPackagePositive.kt"),
            )
        PackageRules.onlyConfigurationsInConfigPackageRule.verify(positiveScope)
    }

    @Test
    fun `onlyConfigurationsInConfigPackageRule detects @ConfigurationProperties-only class in config package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/packages/config/ConfigurationPropertiesInConfigPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.onlyConfigurationsInConfigPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Only @Configuration, @ControllerAdvice, or @RestControllerAdvice classes should be in .config or .configuration package: ConfigurationPropertiesInConfigPackage (fixtures.violations.packages.config)",
            error.message,
        )
    }
}
