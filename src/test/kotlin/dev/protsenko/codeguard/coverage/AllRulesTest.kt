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
        val config = springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            all()
        }

        val expected = allCoreRules + allJpaRules + allNamingRules + allPackageRules + allWebRules
        assertEquals(expected.size, config.getAllRules().size)
        assertTrue(config.getAllRules().containsAll(expected))
    }

    @Test
    fun `all() registers the same rules as configuring each category individually`() {
        val withAll = springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            all()
        }

        val withIndividual = springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            general {
                noFieldInjection()
                statelessConfiguration()
                beanMethodsInConfiguration()
                customExceptionStructure()
                useLoggerNotPrintln()
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
                componentNamingConvention()
                exceptionHandlerNaming()
            }
            packages {
                packageNamingConvention()
                serviceInServicePackage()
                controllerInControllerPackage()
                configurationInConfigPackage()
                configurationPropertiesInPropertiesPackage()
                entitiesInEntityPackage()
            }
            web {
                restControllerReturnTypes()
                properHttpMethodAnnotations()
                noTrailingSlashesInPaths()
                separateDtosFromEntities()
                validateRequestBody()
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
    fun `allCoreRules contains 5 rules`() {
        assertEquals(5, allCoreRules.size)
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
    fun `allPackageRules contains 6 rules`() {
        assertEquals(6, allPackageRules.size)
    }

    @Test
    fun `allWebRules contains 6 rules`() {
        assertEquals(6, allWebRules.size)
    }
}
