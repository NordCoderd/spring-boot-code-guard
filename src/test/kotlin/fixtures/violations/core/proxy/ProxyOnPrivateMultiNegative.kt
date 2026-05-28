package fixtures.violations.core.proxy

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrivateProxyAlphaService {
    @Transactional
    private fun doWork() {}
}

@Service
class PrivateProxyBetaService {
    @Cacheable("cache")
    private fun compute(): String = ""
}
