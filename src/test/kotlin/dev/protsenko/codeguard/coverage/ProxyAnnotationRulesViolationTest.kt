package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.general.CoreRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProxyAnnotationRulesViolationTest {

    @Test
    fun `noProxyAnnotationsOnPrivateMethodsRule detects private @Transactional method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/ProxyAnnotationOnPrivateMethodNegativeTransactional.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.noProxyAnnotationsOnPrivateMethodsRule.verify(scope)
        }
        assertEquals(
            "TransactionalPrivateService.processInternal is private and annotated with @Transactional — " +
                "Spring proxy cannot intercept private methods, the annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noProxyAnnotationsOnPrivateMethodsRule detects private @Cacheable method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/ProxyAnnotationOnPrivateMethodNegativeCacheable.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.noProxyAnnotationsOnPrivateMethodsRule.verify(scope)
        }
        assertEquals(
            "CacheablePrivateService.findCached is private and annotated with @Cacheable — " +
                "Spring proxy cannot intercept private methods, the annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noProxyAnnotationsOnPrivateMethodsRule detects private @Async method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/ProxyAnnotationOnPrivateMethodNegativeAsync.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.noProxyAnnotationsOnPrivateMethodsRule.verify(scope)
        }
        assertEquals(
            "AsyncPrivateService.runInBackground is private and annotated with @Async — " +
                "Spring proxy cannot intercept private methods, the annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noProxyAnnotationsOnPrivateMethodsRule passes for public proxy-annotated methods`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/ProxyAnnotationOnPrivateMethodPositive.kt"),
        )
        CoreRules.noProxyAnnotationsOnPrivateMethodsRule.verify(scope)
    }

    @Test
    fun `noProxyAnnotationsOnPrivateMethodsRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/ProxyAnnotationOnPrivateMethodSuppressedFixture.kt"),
        )
        CoreRules.noProxyAnnotationsOnPrivateMethodsRule.verify(scope)
    }
}
