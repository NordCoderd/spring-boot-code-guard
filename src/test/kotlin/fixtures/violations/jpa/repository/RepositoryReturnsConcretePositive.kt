package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface ConcreteRepository : JpaRepository<RepoSampleDto, Long> {
    fun findByName(name: String): RepoSampleDto

    fun total(): Long
}
