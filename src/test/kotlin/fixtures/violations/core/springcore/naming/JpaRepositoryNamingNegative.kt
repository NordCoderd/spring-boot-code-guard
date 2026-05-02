package fixtures.violations.core.springcore.naming

import org.springframework.data.jpa.repository.JpaRepository

interface BadJpaRepo : JpaRepository<Any, Long> // Violation: Spring Data repo without @Repository, name doesn't end with "Repository"
