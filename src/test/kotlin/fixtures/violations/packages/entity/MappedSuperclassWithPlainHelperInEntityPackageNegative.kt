package fixtures.violations.packages.entity

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

class PlainHelperNextToMappedSuperclass

@MappedSuperclass
abstract class MappedSuperclassWithPlainHelperInEntityPackageNegative {
    @Id
    var id: Long = 0
}
