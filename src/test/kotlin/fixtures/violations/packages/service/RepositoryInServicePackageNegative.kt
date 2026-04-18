package fixtures.violations.packages.service

import org.springframework.stereotype.Repository

@Repository
class RepositoryInServicePackage  // Violation: @Repository should not be in .service package
