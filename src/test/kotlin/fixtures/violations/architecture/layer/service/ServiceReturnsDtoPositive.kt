package fixtures.violations.architecture.layer.service

import org.springframework.stereotype.Service

@Service
class DtoReturningService {
    fun find(): SampleDto = SampleDto()

    fun count(): Long = 0L
}
