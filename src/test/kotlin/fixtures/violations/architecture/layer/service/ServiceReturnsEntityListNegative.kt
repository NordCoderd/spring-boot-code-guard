package fixtures.violations.architecture.layer.service

import org.springframework.stereotype.Service

@Service
class EntityListReturningService {
    fun all(): List<SampleEntity> = emptyList()
}
