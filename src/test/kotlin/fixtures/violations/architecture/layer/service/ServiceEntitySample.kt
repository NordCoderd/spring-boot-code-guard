package fixtures.violations.architecture.layer.service

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
open class SampleEntity(
    @Id
    var id: Long? = null,
    var name: String = "",
)
