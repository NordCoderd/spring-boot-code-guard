package fixtures.violations.performance.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService {
    @Transactional
    fun getUser(id: Long): User? = null

    @Transactional
    fun findAll(): List<User> = emptyList()
}

data class User(val id: Long, val name: String)
