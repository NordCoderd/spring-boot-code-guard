package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationOfProxyService {
    @Transactional
    fun outer() {
        inner()
    }

    @Transactional
    fun inner() {}
}
