package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationLambdaParamShadowService {
    @Transactional
    fun outer() {
        val callbacks: List<() -> Unit> = listOf({})
        callbacks.forEach { inner -> inner() }
    }

    @Transactional
    fun inner() {}
}
