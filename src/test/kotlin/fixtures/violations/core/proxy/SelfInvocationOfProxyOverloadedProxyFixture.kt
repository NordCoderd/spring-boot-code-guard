package fixtures.violations.core.proxy

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelfInvocationOverloadedProxyService {
    @Transactional
    fun process() {
        process(1L)
    }

    @Cacheable("cache")
    fun process(id: Long) {
        require(id > 0)
    }
}
