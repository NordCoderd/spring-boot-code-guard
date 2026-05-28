package fixtures.violations.jpa.entity.domain

import jakarta.persistence.Entity

@Entity
class MultiEntityWithoutIdAlpha {
    var name: String = ""
}

@Entity
class MultiEntityWithoutIdBeta {
    var title: String = ""
}
