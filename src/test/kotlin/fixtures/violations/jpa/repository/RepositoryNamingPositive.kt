package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

data class User(val id: Long)

@Repository
interface UserRepository : JpaRepository<User, Long>  // Correct: ends with "Repository"
