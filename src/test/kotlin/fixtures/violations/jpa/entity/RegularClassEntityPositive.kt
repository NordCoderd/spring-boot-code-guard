package fixtures.violations.jpa.entity.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
open class RegularClassEntity(  // Correct: regular open class, not data class
    @Id
    var id: Long? = null,
    var name: String = "",
    var email: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegularClassEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
