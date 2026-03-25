package fixtures.violations.jpa.entity.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class DataClassEntity(  // Violation: data class used as @Entity
    @Id
    val id: Long,
    val name: String,
    val email: String
)
