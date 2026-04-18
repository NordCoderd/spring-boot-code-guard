package fixtures.violations.packages.configuration

import org.springframework.stereotype.Component

@Component
class ComponentInConfigurationPackage // Violation: @Component should not be in .configuration package
