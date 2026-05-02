package fixtures.violations.packages.repository

import org.springframework.stereotype.Component

@Component
class ComponentInRepositoryPackage // Violation: @Component should not be in .repository package
