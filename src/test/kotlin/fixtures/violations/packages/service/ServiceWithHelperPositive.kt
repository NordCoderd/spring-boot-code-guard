package fixtures.violations.packages.service

import org.springframework.stereotype.Service

data class ServiceRequest(val value: String)
data class ServiceResponse(val result: String)

@Service
class ServiceWithHelper {  // Correct: @Service with helper data classes in the same file
    fun execute(request: ServiceRequest): ServiceResponse = ServiceResponse(request.value)
}
