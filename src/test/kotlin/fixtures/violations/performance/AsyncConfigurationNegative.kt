package fixtures.violations.performance

@org.springframework.stereotype.Component
class AsyncProcessor {
    @org.springframework.scheduling.annotation.Async
    fun processData() {
        // processing logic
    }
}
