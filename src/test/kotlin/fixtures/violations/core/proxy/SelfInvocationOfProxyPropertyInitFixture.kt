package fixtures.violations.core.proxy

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class SelfInvocationPropertyInitService {
    val cache: String = compute()

    @Cacheable("x")
    fun compute(): String = ""
}
