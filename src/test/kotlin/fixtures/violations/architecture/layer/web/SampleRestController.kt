package fixtures.violations.architecture.layer.web

import org.springframework.web.bind.annotation.RestController

@RestController
class SampleRestController {
    fun handle(): String = "ok"
}
