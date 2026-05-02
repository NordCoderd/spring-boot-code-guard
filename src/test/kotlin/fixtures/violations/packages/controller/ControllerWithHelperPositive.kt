package fixtures.violations.packages.controller

import org.springframework.web.bind.annotation.RestController

data class ControllerRequest(
    val value: String,
)

data class ControllerResponse(
    val result: String,
)

@RestController
class ControllerWithHelper // Correct: @RestController with helper data classes in the same file
