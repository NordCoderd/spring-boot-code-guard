package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface StatsRepository : JpaRepository<RepoSampleDto, Long> {
    fun stats(): Map<String, Any>
}
