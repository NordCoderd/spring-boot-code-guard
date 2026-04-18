package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.core.springBootRules
import dev.protsenko.codeguard.rules.general.allCoreRules
import dev.protsenko.codeguard.rules.jpa.allJpaRules
import dev.protsenko.codeguard.rules.naming.allNamingRules
import dev.protsenko.codeguard.rules.packages.allPackageRules
import dev.protsenko.codeguard.rules.web.allWebRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllRulesTest {
    @Test
    fun `all() registers every rule from every category`() {
        val config =
            springBootRules {
                scope = Konsist.scopeFromFiles(emptyList())
                all()
            }

        val expected = allCoreRules + allJpaRules + allNamingRules + allPackageRules + allWebRules
        assertEquals(expected.size, config.getAllRules().size)
        assertTrue(config.getAllRules().containsAll(expected))
    }

    @Test
    fun `all() registers the same rules as configuring each category individually`() {
        val withAll =
            springBootRules {
                scope = Konsist.scopeFromFiles(emptyList())
                all()
            }

        val withIndividual =
            springBootRules {
                scope = Konsist.scopeFromFiles(emptyList())
                general {
                    noFieldInjection()
                    statelessConfiguration()
                    beanMethodsInConfiguration()
                    customExceptionStructure()
                    useLoggerNotPrintln()
                    noStackTracePrint()
                    noProxyAnnotationsOnPrivateMethods()
                }
                jpa {
                    entitiesHaveIdField()
                    noDataClassEntities()
                    transactionalOnServiceLayer()
                    domainLayerNoDependencies()
                }
                naming {
                    serviceNamingConvention()
                    repositoryNamingConvention()
                    controllerNamingConvention()
                    exceptionHandlerNaming()
                    configurationPropertiesNaming()
                }
                packages {
                    packageNamingConvention()
                    serviceInServicePackage()
                    controllerInControllerPackage()
                    configurationInConfigPackage()
                    configurationPropertiesInPropertiesPackage()
                    configurationPropertiesPrefixKebabCase()
                    entitiesInEntityPackage()
                    onlyServicesInServicePackage()
                    onlyEntitiesInEntityPackage()
                    onlyControllersInControllerPackage()
                    onlyConfigurationsInConfigPackage()
                    onlyPropertiesInPropertyPackage()
                    repositoryInRepositoryPackage()
                    onlyRepositoriesInRepositoryPackage()
                }
                web {
                    restControllerReturnTypes()
                    properHttpMethodAnnotations()
                    noTrailingSlashesInPaths()
                    separateDtosFromEntities()
                    controllersDoNotAccessRepositories()
                }
            }

        assertEquals(withIndividual.getAllRules().size, withAll.getAllRules().size)
        assertTrue(withAll.getAllRules().containsAll(withIndividual.getAllRules()))
    }

    @Test
    fun `all() passes verify() on empty scope`() {
        springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            all()
        }.verify()
    }

    @Test
    fun `allCoreRules contains 7 rules`() {
        assertEquals(7, allCoreRules.size)
    }

    @Test
    fun `allJpaRules contains 4 rules`() {
        assertEquals(4, allJpaRules.size)
    }

    @Test
    fun `allNamingRules contains 5 rules`() {
        assertEquals(5, allNamingRules.size)
    }

    @Test
    fun `allPackageRules contains 14 rules`() {
        assertEquals(14, allPackageRules.size)
    }

    @Test
    fun `allWebRules contains 5 rules`() {
        assertEquals(5, allWebRules.size)
    }
}
