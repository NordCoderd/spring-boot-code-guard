package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationConstructorService {
    constructor(flag: Boolean) {
        require(flag)
        inner()
    }

    @Transactional
    fun inner() {
    }
}
