package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val CHAIN_ROOT_PREFIX = "my-app"
const val CHAIN_ALIAS_PREFIX = CHAIN_ROOT_PREFIX

@ConfigurationProperties(prefix = CHAIN_ALIAS_PREFIX)
data class ChainedConstProperties(
    val enabled: Boolean = true,
)
