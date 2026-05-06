package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationAliasReceiverService {
    fun outer() {
        val self = this
        self.inner()
    }

    @Transactional
    fun inner() {
    }
}
