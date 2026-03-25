package fixtures.violations

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NotInControllerPackageController {  // Violation: not in .controller or .web package
    @GetMapping("/data")
    fun getData(): String = "data"
}
