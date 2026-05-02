package fixtures.violations.packages.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

data class EntityAddress(
    val street: String,
)

@Entity
class EntityWithHelper {
    @Id
    var id: Long = 0
}
