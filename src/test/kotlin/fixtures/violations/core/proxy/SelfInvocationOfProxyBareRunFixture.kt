package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationBareRunService {
    @Transactional
    fun outer() {
        run {
            inner()
        }
    }

    @Transactional
    fun inner() {}
}
