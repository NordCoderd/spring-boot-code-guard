package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationApplyReceiverShadowService {
    @Transactional
    fun outer() {
        ApplyReceiverCollaborator().apply { inner() }
    }

    @Transactional
    fun inner() {}
}

class ApplyReceiverCollaborator {
    fun inner() {}
}
