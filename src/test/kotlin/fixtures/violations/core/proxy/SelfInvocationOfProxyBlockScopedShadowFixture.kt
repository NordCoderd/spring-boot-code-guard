package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationBlockScopedShadowService {
    @Transactional
    fun outer() {
        run {
            val inner = Runnable { }
            inner.run()
        }
        inner()
    }

    @Transactional
    fun inner() {}
}
