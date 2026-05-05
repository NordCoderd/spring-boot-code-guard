package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val UPPERCASE_APP_CONFIG_ROOT = "MYAPP"

@ConfigurationProperties("$UPPERCASE_APP_CONFIG_ROOT.foo")
data class UppercaseFooProperties(
    val bar: String = "baz",
)
