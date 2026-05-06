package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationForLoopVarShadowService {
    @Transactional
    fun outer() {
        val callbacks: List<() -> Unit> = emptyList()
        for (inner in callbacks) {
            inner()
        }
    }

    @Transactional
    fun inner() {}
}
