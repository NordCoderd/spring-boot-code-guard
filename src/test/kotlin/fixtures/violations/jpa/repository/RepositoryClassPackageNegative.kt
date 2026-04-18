package fixtures.violations.jpa

import org.springframework.stereotype.Repository

@Repository
class RepositoryClassNotInRepoPackage // Violation: @Repository class not in .repository package
