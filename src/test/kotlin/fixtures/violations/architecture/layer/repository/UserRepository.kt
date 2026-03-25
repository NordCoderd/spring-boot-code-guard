package fixtures.violations.architecture.layer.repository

import org.springframework.stereotype.Repository

@Repository
class UserRepository {  // Changed to class instead of interface
    fun findAll(): List<Any> = emptyList()
}
