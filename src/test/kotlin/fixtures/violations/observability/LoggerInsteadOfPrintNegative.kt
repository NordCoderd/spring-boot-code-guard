package fixtures.violations.observability.service

import org.springframework.stereotype.Service

@Service
class PrintlnService {
    fun processData(data: String) {
        println("Processing data: $data")
        print("Done")
    }
}
