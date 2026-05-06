package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationWithNestedParensService {
    @Transactional
    fun outer() {
        with(buildContext()) {
            inner()
        }
    }

    private fun buildContext(): WithContextHolder = WithContextHolder()

    @Transactional
    fun inner() {}
}

class WithContextHolder {
    fun inner() {}
}
