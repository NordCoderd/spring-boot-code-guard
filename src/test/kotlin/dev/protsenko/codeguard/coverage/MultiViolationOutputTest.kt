package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.core.springBootRules
import org.junit.jupiter.api.Test

class MultiViolationOutputTest {

    @Test
    fun `show error output for multiple violations across rules`() {
        try {
            springBootRules {
                scope = Konsist.scopeFromFiles(
                    listOf("src/test/kotlin/fixtures/violations/core/proxy/MultipleViolationsFixture.kt"),
                )
                general {
                    noFieldInjection()
                    noProxyAnnotationsOnPrivateMethods()
                }
            }.verify()
        } catch (e: AssertionError) {
            println("=== ERROR OUTPUT ===")
            println(e.message)
            println("===================")
        }
    }
}
