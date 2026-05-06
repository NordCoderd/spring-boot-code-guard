package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Suppress("CodeGuard:noSelfInvocationOfProxyMethods")
@Service
class SelfInvocationSuppressedService {
    @Transactional
    fun outer() {
        inner()
    }

    @Transactional
    fun inner() {}
}
