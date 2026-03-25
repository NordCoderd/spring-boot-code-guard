package fixtures.violations.core.proxy

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ValidProxyAnnotationService {
    @Transactional
    fun save(value: String) {}

    @Cacheable("items")
    fun findById(id: Long): String = ""

    @CacheEvict("items")
    fun evict(id: Long) {}

    @CachePut("items")
    fun update(id: Long): String = ""

    @Async
    fun sendNotification() {}
}
