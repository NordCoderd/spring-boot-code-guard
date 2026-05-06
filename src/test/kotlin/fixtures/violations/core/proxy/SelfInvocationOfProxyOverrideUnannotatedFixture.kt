package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

abstract class TransactionalBaseService {
    @Transactional
    open fun inner() {
    }
}

@Service
class SelfInvocationOverrideUnannotatedService : TransactionalBaseService() {
    fun outer() {
        inner()
    }

    override fun inner() {}
}
