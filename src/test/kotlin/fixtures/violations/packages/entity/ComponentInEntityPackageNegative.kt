package fixtures.violations.packages.entity

import org.springframework.stereotype.Component

@Component
class ComponentInEntityPackage // Violation: @Component should not be in .entity package
