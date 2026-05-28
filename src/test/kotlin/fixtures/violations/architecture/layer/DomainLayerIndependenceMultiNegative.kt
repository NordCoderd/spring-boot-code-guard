package fixtures.violations.architecture.layer.domain

import org.springframework.stereotype.Component

@Component
class MultiDomainAlpha {
    var name: String = ""
}

@Component
class MultiDomainBeta {
    var title: String = ""
}
