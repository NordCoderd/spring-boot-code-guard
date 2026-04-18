package fixtures.violations.core.springcore.naming

import org.springframework.data.jpa.repository.JpaRepository

interface GoodJpaRepository : JpaRepository<Any, Long> // Correct: Spring Data repo without @Repository, name ends with "Repository"
