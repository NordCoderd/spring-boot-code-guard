package fixtures.violations.packages

import org.springframework.data.jpa.repository.JpaRepository

interface JpaRepositoryNotInRepoPackage : JpaRepository<Any, Long> // Violation: Spring Data repo not in .repository package
