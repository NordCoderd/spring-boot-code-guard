package fixtures.violations.packages.repository

import org.springframework.stereotype.Repository

data class RepositoryQuery(
    val filter: String,
)

@Repository
class RepositoryWithHelper // Correct: @Repository with helper data class in the same file
