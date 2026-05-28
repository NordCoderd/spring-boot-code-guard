package fixtures.violations.core.config.prefix

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("appAlpha")
data class MultiKebabAlphaProperties(
    val timeout: Int = 0,
)

@ConfigurationProperties("appBeta")
data class MultiKebabBetaProperties(
    val retries: Int = 0,
)
