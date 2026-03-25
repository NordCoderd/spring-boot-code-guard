package fixtures.violations.core.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConfigService {
    @Value("\${app.name}")
    private lateinit var appName: String

    @Value("\${app.version}")
    private lateinit var appVersion: String

    @Value("\${app.timeout}")
    private var timeout: Int = 0
}
