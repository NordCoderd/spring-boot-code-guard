package fixtures.violations.packages.entity

import jakarta.persistence.Embeddable

class PlainHelperNextToEmbeddable

@Embeddable
data class EmbeddableWithPlainHelperInEntityPackageNegative(
    val value: String = "ok",
)
