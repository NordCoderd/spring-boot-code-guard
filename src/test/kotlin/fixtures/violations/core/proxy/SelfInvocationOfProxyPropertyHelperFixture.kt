package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationPropertyHelperService {
    val cache: String = helper()

    private fun helper(): String = inner()

    @Transactional
    fun inner(): String = "ok"
}
