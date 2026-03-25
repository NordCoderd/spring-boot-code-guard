package fixtures.violations.jpa.entity.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class EntityWithoutEqualsHashCode {  // Violation: no equals/hashCode
    @Id
    var id: Long = 0
    var name: String = ""
}
