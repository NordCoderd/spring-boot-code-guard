package fixtures.violations.performance.service.positive

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService {
    @Transactional(readOnly = true)
    fun getUser(id: Long): User? = null

    @Transactional(readOnly = true)
    fun findAll(): List<User> = emptyList()

    @Transactional
    fun saveUser(user: User): User = user
}

data class User(val id: Long, val name: String)
