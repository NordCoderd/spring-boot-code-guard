package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val APP_CONFIG_ROOT = "myapp"

@ConfigurationProperties("$APP_CONFIG_ROOT.foo")
data class FooProperties(
    val bar: String = "baz",
)
