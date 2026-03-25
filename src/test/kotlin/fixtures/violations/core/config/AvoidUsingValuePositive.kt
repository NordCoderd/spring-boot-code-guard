package fixtures.violations.core.service.positive

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConfigService {
    @Value("\${app.name}")
    private lateinit var appName: String

    fun getAppName(): String = appName
}
