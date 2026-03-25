package fixtures.violations.observability.service

import org.springframework.stereotype.Service

@Service
class StackTracePrintingService {
    fun processData(data: String) {
        try {
            require(data.isNotBlank())
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }
}
