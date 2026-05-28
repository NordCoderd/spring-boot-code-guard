package fixtures.violations.observability

import org.springframework.stereotype.Service

@Service
class StackTraceAlphaService {
    fun doWork() {
        try {
            error("fail")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Service
class StackTraceBetaService {
    fun process() {
        try {
            error("fail")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
