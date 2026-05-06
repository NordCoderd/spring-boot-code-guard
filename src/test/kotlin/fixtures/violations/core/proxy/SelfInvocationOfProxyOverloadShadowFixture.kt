package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationOverloadShadowService {
    fun outer() {
        inner()
    }

    fun inner() {}

    @Transactional
    fun inner(id: Long) {
        require(id > 0)
    }
}
