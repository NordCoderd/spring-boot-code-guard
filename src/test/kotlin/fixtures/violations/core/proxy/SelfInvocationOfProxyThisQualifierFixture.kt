package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationThisQualifierService {
    @Transactional
    fun outer() {
        this.inner()
    }

    @Transactional
    fun inner() {}
}
