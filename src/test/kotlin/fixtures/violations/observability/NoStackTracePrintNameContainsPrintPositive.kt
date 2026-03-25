package fixtures.violations.observability.service.positive

import org.springframework.stereotype.Service

@Service
class StackTraceReportService {
    fun printStackTraceReport(): String = "ex.printStackTrace() should be logged, not printed"
}
