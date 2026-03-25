package fixtures.violations.architecture.layer.controller

import fixtures.violations.architecture.layer.repository.UserRepository
import org.springframework.web.bind.annotation.RestController

@RestController
class BadController(
    private val userRepository: UserRepository  // Violation: controller directly depends on repository
) {
    fun getUsers() = userRepository.findAll()
}
