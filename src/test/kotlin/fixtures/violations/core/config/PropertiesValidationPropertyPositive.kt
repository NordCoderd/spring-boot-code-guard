package fixtures.violations.core.config.property

@org.springframework.boot.context.properties.ConfigurationProperties
class PropertyAppProperties {
    var name: String = ""
    var version: String = ""
}
