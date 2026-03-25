package fixtures.violations.jpa.entity

import jakarta.persistence.Id

open class BaseEntityWithId(
    @Id
    open var id: Long? = null,
)
