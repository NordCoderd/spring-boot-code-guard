package fixtures.violations.core.settings

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppProperties  // Correct: @ConfigurationProperties class is exempt from the .config package requirement
