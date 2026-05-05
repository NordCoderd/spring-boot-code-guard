package fixtures.violations.exception

import java.util.UUID

class ResourceNotFoundException(
    val resourceId: UUID,
) : RuntimeException("Resource not found: $resourceId")
