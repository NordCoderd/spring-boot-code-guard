package fixtures.violations.architecture.layer.service

import org.springframework.stereotype.Service

@Service
class MultiEntityServiceAlpha {
    fun find(): SampleEntity = SampleEntity()
}

@Service
class MultiEntityServiceBeta {
    fun find(): SampleEntity = SampleEntity()
}
