package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Suppress("CodeGuard:noProxyAnnotationsOnPrivateMethods")
@Service
class SuppressedPrivateTransactionalService {
    @Transactional
    private fun processInternal() {}
}
