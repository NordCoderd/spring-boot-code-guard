package fixtures.violations.packages.entity

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class MappedSuperclassInEntityPackagePositive {
    @Id
    var id: Long = 0
}
