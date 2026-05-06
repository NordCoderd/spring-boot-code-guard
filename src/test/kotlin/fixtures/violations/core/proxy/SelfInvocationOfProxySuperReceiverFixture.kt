package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

abstract class SuperReceiverBaseService {
    @Transactional
    open fun audit() {}
}

@Service
class SelfInvocationSuperReceiverService : SuperReceiverBaseService() {
    @Transactional
    fun process() {
        super.audit()
    }
}
