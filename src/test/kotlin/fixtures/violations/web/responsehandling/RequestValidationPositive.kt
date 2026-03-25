package fixtures.violations.web.controller.positive

@org.springframework.web.bind.annotation.RestController
class UserController {
    @org.springframework.web.bind.annotation.PostMapping("/users")
    fun createUser(
        @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody user: UserDto,
    ): UserDto = user
}

data class UserDto(val name: String, val email: String)
