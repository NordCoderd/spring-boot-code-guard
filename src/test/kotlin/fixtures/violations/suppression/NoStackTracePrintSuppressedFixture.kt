package fixtures.violations.suppression

import org.springframework.stereotype.Service

@Suppress("CodeGuard:noStackTracePrint")
@Service
class SuppressedStackTracePrintingService {
    fun processData(data: String) {
        try {
            require(data.isNotBlank())
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }
}
