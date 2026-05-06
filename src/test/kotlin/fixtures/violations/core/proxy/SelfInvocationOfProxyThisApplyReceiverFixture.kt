package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationThisApplyReceiverService {
    @Transactional
    fun outer() {
        this.apply { inner() }
    }

    @Transactional
    fun inner() {}
}
