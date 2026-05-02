package fixtures.violations.packages.web

import org.springframework.stereotype.Component

@Component
class ComponentInWebPackage // Violation: @Component should not be in .web package
