package fixtures.violations.jpa.entity.domain

import jakarta.persistence.Entity

@Entity
class EntityWithoutId {  // Violation: no @Id field
    var name: String = ""
}
