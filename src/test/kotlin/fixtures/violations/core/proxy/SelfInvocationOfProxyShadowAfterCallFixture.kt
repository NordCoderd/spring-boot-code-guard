package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationShadowAfterCallService {
    @Transactional
    fun outer() {
        inner()
        val inner: () -> Unit = {}
        inner()
    }

    @Transactional
    fun inner() {}
}
