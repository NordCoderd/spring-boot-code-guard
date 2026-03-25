package fixtures.violations.core.proxy

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CacheablePrivateService {
    @Cacheable("users")
    private fun findCached(id: Long): String = ""
}
