package fixtures.violations.core.proxy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService {
    @Transactional
    private fun processOrder() {}
}

@Service
class PaymentService {
    @Cacheable("payments")
    private fun findPayment(id: Long): String = ""
}

@Service
class NotificationService {
    @Autowired
    lateinit var dependency: String
}
