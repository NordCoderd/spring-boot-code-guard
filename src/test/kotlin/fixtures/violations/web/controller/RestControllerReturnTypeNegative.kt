package fixtures.violations.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class VoidReturningController {
    @GetMapping("/void")
    fun voidMethod(): Unit {  // Violation: explicitly returns Unit
        println("test")
    }
}
