package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.core.RuleResult
import dev.protsenko.codeguard.core.memoized
import dev.protsenko.codeguard.core.parseKotlin
import dev.protsenko.codeguard.core.springBootRules
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ScopeMemoizationTest {
    private val fixture = "src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyInheritedFixture.kt"

    @Test
    fun `memoized scope returns the same declaration lists on repeated access`() {
        val scope = Konsist.scopeFromFiles(listOf(fixture)).memoized()

        assertSame(scope.classes(), scope.classes())
        assertSame(scope.interfaces(), scope.interfaces())
        assertSame(scope.classesAndInterfaces(), scope.classesAndInterfaces())
        assertSame(scope.functions(), scope.functions())
        assertSame(scope.properties(), scope.properties())
        assertSame(scope, scope.memoized())
    }

    @Test
    fun `memoized scope exposes the same declarations as the original`() {
        val scope = Konsist.scopeFromFiles(listOf(fixture))

        assertEquals(scope.classes().map { it.name }, scope.memoized().classes().map { it.name })
        assertEquals(scope.functions().map { it.name }, scope.memoized().functions().map { it.name })
    }

    @Test
    fun `parseKotlin shares one parsed tree per source text`() {
        val text = "class A { fun a() = println(\"x\") }"

        assertSame(parseKotlin(text), parseKotlin(text))
    }

    @Test
    fun `verifyWithResults reports the same failures as verify`() {
        val config = springBootRules {
            scope = Konsist.scopeFromFiles(listOf(fixture))
            all()
        }

        val failures = config.verifyWithResults().filterIsInstance<RuleResult.Failure>()
        val thrown = runCatching { config.verify() }.exceptionOrNull()

        assertTrue(failures.isNotEmpty())
        assertTrue(failures.all { thrown?.message?.contains(it.message) == true })
    }
}
