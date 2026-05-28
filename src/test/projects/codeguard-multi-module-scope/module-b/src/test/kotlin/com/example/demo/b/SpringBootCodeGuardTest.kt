package com.example.demo.b

import dev.protsenko.codeguard.core.springBootRules
import kotlin.test.Test

class SpringBootCodeGuardTest {
    @Test
    fun `spring-boot-code-guard all rules`() {
        springBootRules { all() }.verify()
    }
}
