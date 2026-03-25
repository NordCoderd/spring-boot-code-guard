package fixtures.violations.observability.service.positive

@org.springframework.stereotype.Service
class LoggerService {
    fun processData(data: String) {
        logger.info("Processing data: {}", data)
    }

    companion object {
        private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(LoggerService::class.java)
    }
}
