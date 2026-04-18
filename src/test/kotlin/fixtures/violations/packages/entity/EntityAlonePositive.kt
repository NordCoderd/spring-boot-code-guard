package fixtures.violations.packages.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class ProperEntityAlone {
    @Id
    var id: Long = 0
}
