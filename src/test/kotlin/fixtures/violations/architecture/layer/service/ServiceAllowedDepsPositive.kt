package fixtures.violations.architecture.layer.service

import fixtures.violations.architecture.layer.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AllowedDepsService(
    private val userRepository: UserRepository,
) {
    fun all(): List<Any> = userRepository.findAll()
}
