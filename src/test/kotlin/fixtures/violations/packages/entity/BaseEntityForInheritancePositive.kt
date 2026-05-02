package fixtures.violations.packages.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
open class BaseEntityForInheritancePositive {
    @Id
    open var id: Long = 0
}
