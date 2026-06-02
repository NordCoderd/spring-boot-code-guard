package fixtures.violations.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface MultiThingRepository : JpaRepository<RepoSampleDto, Long> {
    fun findOne(): Any

    fun findMany(): List<Any>
}
