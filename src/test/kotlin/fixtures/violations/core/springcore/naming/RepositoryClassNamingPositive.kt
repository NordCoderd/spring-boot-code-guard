package fixtures.violations.core.springcore.naming

import org.springframework.stereotype.Repository

@Repository
class GoodClassRepository {  // Correct: ends with "Repository"
    fun findData(): List<String> = listOf("data")
}
