package fixtures.violations.web.responsehandling.controller

import fixtures.violations.web.responsehandling.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MultiDtoSeparationController {
    @GetMapping("/user/alpha")
    fun getAlpha(): User = User()

    @GetMapping("/user/beta")
    fun getBeta(): User = User()
}
