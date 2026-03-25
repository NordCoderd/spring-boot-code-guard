package fixtures.violations.jpa.repository

import org.springframework.stereotype.Repository

@Repository
interface BadRepoName {  // Violation: doesn't end with "Repository"
    fun findAll(): List<String>
}
