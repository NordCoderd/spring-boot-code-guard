package fixtures.violations.packages.entity

data class DerivedEntityHelper(
    val value: String,
)

class DerivedFromEntityWithHelperPositive : BaseEntityForInheritancePositive()
