package fixtures.violations.jpa.entity.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class EntityWithEqualsHashCode {  // Correct: has equals/hashCode
    @Id
    var id: Long = 0
    var name: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityWithEqualsHashCode) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
