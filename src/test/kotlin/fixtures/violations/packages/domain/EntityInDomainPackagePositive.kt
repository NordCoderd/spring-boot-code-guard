package fixtures.violations.packages.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class ProperEntityInDomainPackage {
    @Id
    var id: Long = 0
}
