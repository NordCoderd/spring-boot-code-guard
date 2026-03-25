package fixtures.violations.web.responsehandling.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
open class User(
    @Id
    var id: Long? = null,
    var name: String = ""
)
