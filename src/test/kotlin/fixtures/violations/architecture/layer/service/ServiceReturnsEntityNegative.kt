package fixtures.violations.architecture.layer.service

import org.springframework.stereotype.Service

@Service
class EntityReturningService {
    fun find(): SampleEntity = SampleEntity()
}
