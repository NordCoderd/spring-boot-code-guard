package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationSafeThenUnsafeHelperService {
    fun outer() {
        safeHelper()
        unsafeHelper()
    }

    private fun safeHelper() {
        println("ok")
    }

    private fun unsafeHelper() {
        inner()
    }

    @Transactional
    fun inner() {
    }
}
