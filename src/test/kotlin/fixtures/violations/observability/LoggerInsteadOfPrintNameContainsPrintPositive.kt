package fixtures.violations.observability.service.positive

import org.springframework.stereotype.Service

@Service
class BlueprintService {
    fun processData(data: String): String {
        return blueprint(data)
    }

    private fun blueprint(value: String): String = "processed-$value"
}
