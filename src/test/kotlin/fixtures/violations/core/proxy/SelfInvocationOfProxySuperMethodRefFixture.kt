package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

abstract class SuperMethodRefBaseService {
    @Transactional
    open fun audit() {
    }
}

@Service
class SelfInvocationSuperMethodRefService : SuperMethodRefBaseService() {
    fun process() {
        super.audit()
    }
}
