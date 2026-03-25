package fixtures.violations.architecture.layer.entity

class CleanEntityDomainObject {  // Correct: no Spring dependencies
    var id: Long? = null
    var name: String = ""
}
