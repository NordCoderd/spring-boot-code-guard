package fixtures.violations.packages.domain

import org.springframework.stereotype.Component

@Component
class ComponentInDomainPackage // Violation: @Component should not be in .domain package
