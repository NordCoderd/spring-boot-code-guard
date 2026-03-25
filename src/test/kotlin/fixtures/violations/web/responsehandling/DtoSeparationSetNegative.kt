package fixtures.violations.web.responsehandling.controller

import fixtures.violations.web.responsehandling.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SetEntityController {
    @GetMapping("/users")
    fun getUsers(): Set<User> = setOf(User())
}
