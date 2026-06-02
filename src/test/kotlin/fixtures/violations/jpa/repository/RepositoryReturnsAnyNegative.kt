package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface ThingRepository : JpaRepository<RepoSampleDto, Long> {
    fun findThing(): Any
}
