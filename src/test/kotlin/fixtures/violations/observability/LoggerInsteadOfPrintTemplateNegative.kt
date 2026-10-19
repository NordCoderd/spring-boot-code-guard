package fixtures.violations.observability.service.printtemplate

import org.springframework.stereotype.Service

@Service
class TemplatePrintService {
    fun describe(data: String): String = "result: ${println(data)}"
}
