package fixtures.violations.web.responsehandling.controller

import fixtures.violations.web.responsehandling.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EntityInControllerController {
    @GetMapping("/user")
    fun getUser(): ResponseEntity<User> {  // Violation: returns JPA entity
        return ResponseEntity(User())
    }
}
