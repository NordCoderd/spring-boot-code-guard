package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val CHAIN_INVALID_ROOT = "MyApp"
const val CHAIN_INVALID_ALIAS = CHAIN_INVALID_ROOT

@ConfigurationProperties(prefix = CHAIN_INVALID_ALIAS)
data class ChainedInvalidConstProperties(
    val enabled: Boolean = true,
)
