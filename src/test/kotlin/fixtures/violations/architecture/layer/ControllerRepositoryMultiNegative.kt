package fixtures.violations.architecture.layer.controller

import fixtures.violations.architecture.layer.repository.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MultiControllerAlpha(private val userRepository: UserRepository) {
    @GetMapping("/alpha")
    fun get(): List<Any> = userRepository.findAll()
}

@RestController
class MultiControllerBeta(private val userRepository: UserRepository) {
    @GetMapping("/beta")
    fun get(): List<Any> = userRepository.findAll()
}
