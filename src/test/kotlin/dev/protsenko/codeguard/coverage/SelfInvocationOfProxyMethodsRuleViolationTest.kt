package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.proxy.ProxyRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SelfInvocationOfProxyMethodsRuleViolationTest {

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects @Transactional calling @Transactional in same class`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyNegativeFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationOfProxyService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects @Transactional calling @Cacheable in same class`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyMixedAnnotationsFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationMixedProxyService.outer is annotated with @Transactional and invokes " +
                    "@Cacheable method cached of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when proxy methods do not call siblings`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/ProxyAnnotationOnPrivateMethodPositive.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when call goes through foreign receiver`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyForeignReceiverFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when sibling name is shadowed by local variable`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxySuppressedFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects this-qualified self-invocation`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyThisQualifierFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationThisQualifierService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects self-invocation inside string template`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyStringTemplateFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationStringTemplateService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes for direct recursion of the same proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyRecursionFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when sibling name appears only in comments and strings`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyCommentsAndStringsFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when sibling name is shadowed by lambda parameter`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyLambdaParamShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when sibling name is shadowed by for-loop variable`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyForLoopVarShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when sibling name is shadowed by destructured component`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyDestructuredShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when bare call lives inside apply block on foreign receiver`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyApplyReceiverShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects self-invocation inside apply on this receiver`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyThisApplyReceiverFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationThisApplyReceiverService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects call before sibling name is shadowed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyShadowAfterCallFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationShadowAfterCallService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects self-invocation of inherited proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyInheritedFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationInheritedService.process is annotated with @Transactional and invokes " +
                    "@Transactional method audit of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects self-invocation from init block`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyInitBlockFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationInitBlockService init block invokes @Transactional method warmup of the " +
                    "same class — Spring AOP proxy is bypassed during initialization, the annotation will be " +
                    "silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects self-invocation from property initializer`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyPropertyInitFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationPropertyInitService property 'cache' initializer invokes @Cacheable method " +
                    "compute of the same class — Spring AOP proxy is bypassed during initialization, the " +
                    "annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects method reference to sibling proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyMethodRefFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationMethodRefService.outer is annotated with @Transactional and captures a " +
                    "method reference to @Transactional method inner of the same class — invoking the " +
                    "reference bypasses Spring AOP proxy, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when with block uses receiver expression with parens`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyWithNestedParensFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects self-invocation inside string template URL`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyStringTemplateUrlFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationStringTemplateUrlService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects call after block-scoped local shadow ends`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyBlockScopedShadowFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationBlockScopedShadowService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects public method calling proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyPublicCallerFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationPublicCallerService.outer invokes @Transactional method inner of the same class — " +
                    "Spring AOP proxy is bypassed on self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects private helper calling proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyPublicPrivateCallerFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationPublicPrivateCallerService.helper invokes @Transactional method inner of the same class — " +
                    "Spring AOP proxy is bypassed on self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects unsafe helper after safe helper`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxySafeThenUnsafeHelperFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationSafeThenUnsafeHelperService.unsafeHelper invokes @Transactional method inner of the same class — " +
                    "Spring AOP proxy is bypassed on self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when unannotated overload is called`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyOverloadShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when function parameter shadows proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyParameterShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when nested local function shadows proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyNestedLocalFunctionShadowFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes when unannotated override shadows inherited proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyOverrideUnannotatedFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects this-qualified method reference`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyThisMethodRefFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationThisMethodRefService.outer captures a method reference to @Transactional method " +
                    "inner of the same class — invoking the reference bypasses Spring AOP proxy, the inner annotation " +
                    "will be silently ignored.",
            error.message,
        )
    }


    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects super-qualified proxy call from unannotated method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxySuperMethodRefFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationSuperMethodRefService.process invokes @Transactional method audit of the same class — " +
                    "Spring AOP proxy is bypassed on self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes for method reference through same-instance alias`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyCallableReferenceAliasFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule passes for proxy call through same-instance alias`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyAliasReceiverFixture.kt"),
        )
        ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects direct proxy call from constructor`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyConstructorFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationConstructorService constructor invokes @Transactional method inner of the same class — " +
                    "Spring AOP proxy is bypassed during initialization, the annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects helper called from init block reaching proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyInitHelperFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationInitHelperService.helper invokes @Transactional method inner of the same class — " +
                    "Spring AOP proxy is bypassed on self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects helper called from property initializer reaching proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyPropertyHelperFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationPropertyHelperService.helper invokes @Transactional method inner of the same class — " +
                    "Spring AOP proxy is bypassed on self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects helper called from constructor reaching proxy method`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyConstructorHelperFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationConstructorHelperService.helper invokes @Transactional method inner of the same class — " +
                    "Spring AOP proxy is bypassed on self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects overloaded proxy method call with correct arity`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyOverloadedProxyFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationOverloadedProxyService.process is annotated with @Transactional and invokes " +
                    "@Cacheable method process of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects proxy call inside bare run block`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyBareRunFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationBareRunService.outer is annotated with @Transactional and invokes " +
                    "@Transactional method inner of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }

    @Test
    fun `noSelfInvocationOfProxyMethodsRule detects super-qualified self-invocation`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxySuperReceiverFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
        }
        assertEquals(
            "SelfInvocationSuperReceiverService.process is annotated with @Transactional and invokes " +
                    "@Transactional method audit of the same class — Spring AOP proxy is bypassed on " +
                    "self-invocation, the inner annotation will be silently ignored.",
            error.message,
        )
    }
}
