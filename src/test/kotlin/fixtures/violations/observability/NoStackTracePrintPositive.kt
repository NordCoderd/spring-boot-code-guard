package fixtures.violations.observability.service.positive

@org.springframework.stereotype.Service
class StackTraceLoggingService {
    fun processData(data: String) {
        try {
            require(data.isNotBlank())
        } catch (ex: IllegalArgumentException) {
            logger.warn("Invalid data", ex)
        }
    }

    companion object {
        private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(StackTraceLoggingService::class.java)
    }
}
