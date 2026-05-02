package fixtures.violations.packages.repository

import org.springframework.data.jpa.repository.JpaRepository

interface JpaRepositoryWithoutAnnotation : JpaRepository<Any, Long> // Should pass: Spring Data repository without explicit @Repository
