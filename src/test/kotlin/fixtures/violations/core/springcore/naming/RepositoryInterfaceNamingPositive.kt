package fixtures.violations.core.springcore.naming

import org.springframework.stereotype.Repository

@Repository
interface GoodRepository {  // Correct: ends with "Repository"
    fun findData(): List<String>
}
