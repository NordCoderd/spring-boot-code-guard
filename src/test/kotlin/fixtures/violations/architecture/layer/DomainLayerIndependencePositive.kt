package fixtures.violations.architecture.layer.domain

class CleanDomainEntity {  // Correct: no Spring dependencies
    var id: Long? = null
    var name: String = ""
}
