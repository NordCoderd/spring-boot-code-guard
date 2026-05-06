package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationForeignReceiverService(
    private val collaborator: CollaboratorService,
) {
    @Transactional
    fun outer() {
        collaborator.inner()
    }

    @Transactional
    fun inner() {}
}

@Service
class CollaboratorService {
    @Transactional
    fun inner() {}
}
