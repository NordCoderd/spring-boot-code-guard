package fixtures.violations.jpa.entity

import jakarta.persistence.Entity

@Entity
open class InheritedIdEntity(
    var name: String = "",
) : BaseEntityWithId()
