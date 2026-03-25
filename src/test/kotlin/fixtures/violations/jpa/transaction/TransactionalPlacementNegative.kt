package fixtures.violations.jpa.transaction

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Transactional  // Violation: @Transactional on controller
class TransactionalController {
    @GetMapping("/tx")
    fun getData(): String = "data"
}
