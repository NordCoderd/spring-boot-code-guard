package fixtures.violations.core.springcore.naming

import org.springframework.stereotype.Repository

@Repository
interface BadRepoInterface {  // Violation: doesn't end with "Repository"
    fun findData(): List<String>
}
