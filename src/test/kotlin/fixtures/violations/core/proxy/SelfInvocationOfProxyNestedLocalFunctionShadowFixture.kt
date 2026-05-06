package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationNestedLocalFunctionShadowService {
    fun outer() {
        fun inner() {}
        inner()
    }

    @Transactional
    fun inner() {
    }
}
