package fixtures.violations.jpa.entity.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
open class SimpleEntity {  // Correct: regular open class without primary constructor
    @Id
    var id: Long? = null

    var name: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
