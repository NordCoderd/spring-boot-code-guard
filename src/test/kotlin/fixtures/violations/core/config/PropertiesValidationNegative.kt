package fixtures.violations.core.wrongpackage

@org.springframework.boot.context.properties.ConfigurationProperties
class AppProperties {
    var name: String = ""
    var version: String = ""
}
