package fixtures.violations.web.responsehandling.controller

import fixtures.violations.web.responsehandling.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BothViolationsController {
    @GetMapping("/user/update")
    fun updateAndReturn(user: User): User = user
}
