package fixtures.violations.core.springcore.naming

import org.springframework.stereotype.Component

@Component
class DataProcessorComponent {  // Correct: ends with "Component"
    fun process() = "processing"
}

@Component
class BeanFactory {  // Correct: ends with "Factory"
    fun createBean() = "bean"
}

@Component
class DataProvider {  // Correct: ends with "Provider"
    fun provideData() = "data"
}

@Component
class EventListener {  // Correct: ends with "Listener"
    fun onEvent() = "event"
}

@Component
class RequestHandler {  // Correct: ends with "Handler"
    fun handle() = "handled"
}
