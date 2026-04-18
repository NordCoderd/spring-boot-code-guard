package fixtures.violations.packages.service

import org.springframework.stereotype.Component

@Component
class ComponentInServicePackage  // Violation: @Component should not be in .service package
