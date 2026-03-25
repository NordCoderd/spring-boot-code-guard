package fixtures.violations.core.proxy

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionalPrivateService {
    @Transactional
    private fun processInternal() {}
}
