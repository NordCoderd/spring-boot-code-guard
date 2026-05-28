package fixtures.violations.observability

import org.springframework.stereotype.Service

@Service
class PrintlnAlphaService {
    fun doWork() {
        println("working")
    }
}

@Service
class PrintlnBetaService {
    fun process() {
        println("processing")
    }
}
