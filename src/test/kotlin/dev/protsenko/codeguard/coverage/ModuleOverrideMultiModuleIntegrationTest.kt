package dev.protsenko.codeguard.coverage

import org.junit.jupiter.api.Test
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleOverrideMultiModuleIntegrationTest {
    @Test
    fun `moduleName override verifies only production sources from configured Gradle module`() {
        val fixtureProject = File("src/test/projects/codeguard-multi-module-scope").absoluteFile
        val gradle = if (System.getProperty("os.name").startsWith("Windows")) "gradlew.bat" else "./gradlew"
        val process =
            ProcessBuilder(
                gradle,
                "-p",
                fixtureProject.path,
                ":module-a:test",
                "--tests",
                "*CodeGuard*",
                "--no-daemon",
            )
                .directory(File(".").absoluteFile)
                .redirectErrorStream(true)
                .start()

        val finished = process.waitFor(90, TimeUnit.SECONDS)
        if (!finished) {
            process.destroyForcibly()
        }
        assertTrue(finished, "Nested Gradle build timed out")

        val output = process.inputStream.bufferedReader().readText()
        assertEquals(0, process.exitValue(), output)
    }
}
