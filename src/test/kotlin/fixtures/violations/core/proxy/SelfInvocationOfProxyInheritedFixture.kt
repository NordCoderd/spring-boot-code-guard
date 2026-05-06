package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

abstract class BaseAuditService {
    @Transactional
    open fun audit() {}
}

@Service
class SelfInvocationInheritedService : BaseAuditService() {
    @Transactional
    fun process() {
        audit()
    }
}
