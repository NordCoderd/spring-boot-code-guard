package fixtures.violations.packages.controller

import org.springframework.stereotype.Component

@Component
class ComponentInControllerPackage // Violation: @Component should not be in .controller package
