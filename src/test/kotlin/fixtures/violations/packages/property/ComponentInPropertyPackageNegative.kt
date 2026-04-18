package fixtures.violations.packages.property

import org.springframework.stereotype.Component

@Component
class ComponentInPropertyPackage // Violation: @Component should not be in .property package
