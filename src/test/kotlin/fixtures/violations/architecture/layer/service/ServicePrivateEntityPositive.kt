package fixtures.violations.architecture.layer.service

import org.springframework.stereotype.Service

@Service
class PrivateEntityService {
    fun find(): SampleDto = SampleDto()

    private fun loadEntity(): SampleEntity = SampleEntity()
}
