package fixtures.violations.architecture.layer.controller

import fixtures.violations.architecture.layer.repository.UserJpaRepository
import org.springframework.web.bind.annotation.RestController

@RestController
class BadInterfaceController(
    private val userJpaRepository: UserJpaRepository,
) {
    fun getUsers() = userJpaRepository.findAll()
}
