package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val APP_CODE_NAME = "spring"

@ConfigurationProperties("$APP_CODE_NAME")
data class CodeNameProperties(
    val enabled: Boolean = true,
)
