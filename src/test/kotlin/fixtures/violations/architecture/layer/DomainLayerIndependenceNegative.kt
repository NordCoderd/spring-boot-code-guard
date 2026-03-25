package fixtures.violations.architecture.layer.domain

import org.springframework.stereotype.Component  // Violation: domain imports Spring

@Component
class DomainEntity {  // Violation: Spring annotation in domain
    var id: Long? = null
    var name: String = ""
}
