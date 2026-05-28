package fixtures.violations.jpa.transaction

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Transactional
class TransactionalAlphaController {
    @GetMapping("/alpha")
    fun get(): String = "alpha"
}

@RestController
@Transactional
class TransactionalBetaController {
    @GetMapping("/beta")
    fun get(): String = "beta"
}
