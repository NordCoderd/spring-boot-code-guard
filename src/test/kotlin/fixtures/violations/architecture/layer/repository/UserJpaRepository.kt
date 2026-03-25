package fixtures.violations.architecture.layer.repository

import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository {
    fun findAll(): List<Any>
}
