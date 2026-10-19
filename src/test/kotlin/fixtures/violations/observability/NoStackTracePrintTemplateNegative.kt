package fixtures.violations.observability.service.template

import org.springframework.stereotype.Service

@Service
class TemplateStackTraceService {
    fun describe(ex: Exception?): String = "failed: ${ex?.printStackTrace()}"
}
