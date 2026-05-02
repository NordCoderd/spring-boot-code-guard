package fixtures.violations.packages.service

import org.springframework.stereotype.Component

@Component
class ComponentWithNestedHelperInServicePackage {
    private data class NestedServiceHelper(
        val value: String,
    )

    fun load(): String = NestedServiceHelper("value").value
}
