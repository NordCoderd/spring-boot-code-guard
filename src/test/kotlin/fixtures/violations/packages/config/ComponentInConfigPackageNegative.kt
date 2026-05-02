package fixtures.violations.packages.config

import org.springframework.stereotype.Component

@Component
class ComponentInConfigPackage // Violation: @Component should not be in .config package
