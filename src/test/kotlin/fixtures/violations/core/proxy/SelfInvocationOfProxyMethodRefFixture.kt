package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationMethodRefService {
    @Transactional
    fun outer(): () -> Unit = ::inner

    @Transactional
    fun inner() {}
}
