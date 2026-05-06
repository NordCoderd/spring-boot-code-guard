package fixtures.violations.core.proxy

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationMixedProxyService {
    @Transactional
    fun outer() {
        cached()
    }

    @Cacheable("items")
    fun cached(): String = ""
}
