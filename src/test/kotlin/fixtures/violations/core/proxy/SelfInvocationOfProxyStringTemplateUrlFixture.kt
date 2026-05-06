package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationStringTemplateUrlService {
    @Transactional
    fun outer(): String {
        return "https://example.test/${inner()}"
    }

    @Transactional
    fun inner(): String = "x"
}
