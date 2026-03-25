package fixtures.violations.architecture.layer.controller

import fixtures.violations.architecture.layer.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class GoodController(
    private val userService: UserService  // Correct: controller depends on service
) {
    fun getUsers() = userService.getAllUsers()
}
