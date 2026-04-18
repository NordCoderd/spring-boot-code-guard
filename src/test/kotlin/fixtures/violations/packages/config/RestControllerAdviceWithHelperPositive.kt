package fixtures.violations.packages.config

import org.springframework.web.bind.annotation.RestControllerAdvice

data class ControllerAdviceHelper(
    val value: String,
)

@RestControllerAdvice
class RestControllerAdviceWithHelperPositive
