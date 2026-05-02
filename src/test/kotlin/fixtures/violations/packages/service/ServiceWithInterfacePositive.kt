package fixtures.violations.packages.service

import org.springframework.stereotype.Service

interface PaymentService {
    fun pay(): Unit
}

@Service
class PaymentServiceImpl : PaymentService {  // Correct: interface + @Service impl in same file
    override fun pay() = Unit
}
