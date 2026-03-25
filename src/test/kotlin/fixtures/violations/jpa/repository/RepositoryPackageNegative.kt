package fixtures.violations.jpa

import org.springframework.stereotype.Repository

@Repository
interface RepositoryNotInRepoPackage {  // Violation: not in .repository package
    fun findAll(): List<String>
}
