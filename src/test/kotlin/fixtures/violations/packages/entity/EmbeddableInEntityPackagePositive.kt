package fixtures.violations.packages.entity

import jakarta.persistence.Embeddable

@Embeddable
data class EmbeddableInEntityPackagePositive(
    val value: String = "ok",
)
