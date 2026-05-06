package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationPublicPrivateCallerService {
    fun outer() {
        helper()
    }

    private fun helper() {
        inner()
    }

    @Transactional
    fun inner() {
    }
}
