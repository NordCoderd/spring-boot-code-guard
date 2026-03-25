package fixtures.violations.performance.service

@org.springframework.stereotype.Service
class AsyncService {
    @org.springframework.scheduling.annotation.Async
    fun processData() {
        // processing logic
    }
}
