package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationShadowService {
    @Transactional
    fun outer() {
        val inner: () -> Unit = {}
        inner()
    }

    @Transactional
    fun inner() {}
}
