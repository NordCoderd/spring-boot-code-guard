package fixtures.violations.suppression

import org.springframework.data.repository.Repository
import org.springframework.stereotype.Repository as RepositoryAnnotation

@Suppress("CodeGuard:repositoryNaming")
@RepositoryAnnotation
class SuppressedBadName
