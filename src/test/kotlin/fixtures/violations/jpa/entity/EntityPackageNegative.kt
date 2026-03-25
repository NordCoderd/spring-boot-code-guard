package fixtures.violations.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class EntityNotInDomainPackage {  // Violation: not in .domain or .entity package
    @Id
    var id: Long = 0
}
