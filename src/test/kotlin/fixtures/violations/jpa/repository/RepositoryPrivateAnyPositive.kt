package fixtures.violations.jpa.repository

import org.springframework.stereotype.Repository

@Repository
class PrivateRawRepository {
    fun get(): Long = 0L

    private fun raw(): Any = Any()
}
