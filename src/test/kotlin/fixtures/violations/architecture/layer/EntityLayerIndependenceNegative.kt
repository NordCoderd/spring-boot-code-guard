package fixtures.violations.architecture.layer.entity

import org.springframework.stereotype.Component  // Violation: entity imports Spring

@Component
class EntityDomainObject {  // Violation: Spring annotation in entity package
    var id: Long? = null
    var name: String = ""
}
