package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationStringTemplateService {
    @Transactional
    fun outer(): String {
        return "value=${inner()}"
    }

    @Transactional
    fun inner(): String = "x"
}
