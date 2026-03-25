package fixtures.violations.web.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
    @PostMapping("/users")
    fun createUser(
        @org.springframework.web.bind.annotation.RequestBody user: UserDto,
    ): UserDto = user
}

data class UserDto(val name: String, val email: String)
