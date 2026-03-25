package fixtures.violations.core.springcore.naming

import org.springframework.stereotype.Repository

@Repository
class BadRepoClass {  // Violation: doesn't end with "Repository"
    fun findData(): List<String> = listOf("data")
}
