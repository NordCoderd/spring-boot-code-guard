package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface ThingListRepository : JpaRepository<RepoSampleDto, Long> {
    fun findThings(): List<Any>
}
