package fixtures.violations.architecture.layer.controller

import fixtures.violations.architecture.layer.repository.UserRepository
import org.springframework.web.bind.annotation.RestController

@RestController
class BothViolationsController(private val constructorRepository: UserRepository) {
    private val propertyRepository: UserRepository = constructorRepository

    fun getUsers() = constructorRepository.findAll() + propertyRepository.findAll()
}
