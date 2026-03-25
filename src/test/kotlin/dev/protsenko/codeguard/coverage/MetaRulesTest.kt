package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

/**
 * Meta-rules that verify the rule implementations themselves follow suppression conventions.
 */
class MetaRulesTest {

    // \s* handles both inline (scope.classes()) and chained multi-line (scope\n    .classes()) styles
    private val scopeClasses = Regex("""scope\s*\.\s*classes\(\)""")
    private val scopeClassesAndInterfaces = Regex("""scope\s*\.\s*classesAndInterfaces\(\)""")
    private val scopeFunctions = Regex("""scope\s*\.\s*functions\(\)""")
    private val scopeProperties = Regex("""scope\s*\.\s*properties\(\)""")

    // entityIdRule uses scope.classes().associateBy for the parent-lookup map — allowed exception
    private val allowedClassesUsage = Regex("""scope\s*\.\s*classes\(\)\s*\.\s*associateBy""")

    @Test
    fun `rule files must not call scope classes() directly — use notSuppressedClasses()`() {
        Konsist
            .scopeFromPackage("dev.protsenko.codeguard.rules..")
            .files
            .forEach { file ->
                val textWithoutAllowedUsages = file.text.replace(allowedClassesUsage, "")
                assertFalse(
                    scopeClasses.containsMatchIn(textWithoutAllowedUsages),
                    "${file.name} calls scope.classes() directly — use scope.notSuppressedClasses(suppressKey) instead",
                )
            }
    }

    @Test
    fun `rule files must not call scope classesAndInterfaces() directly — use notSuppressedClassesAndInterfaces()`() {
        Konsist
            .scopeFromPackage("dev.protsenko.codeguard.rules..")
            .files
            .forEach { file ->
                assertFalse(
                    scopeClassesAndInterfaces.containsMatchIn(file.text),
                    "${file.name} calls scope.classesAndInterfaces() directly — " +
                        "use scope.notSuppressedClassesAndInterfaces(suppressKey) instead",
                )
            }
    }

    @Test
    fun `rule files must not call scope functions() directly — use notSuppressedFunctions()`() {
        Konsist
            .scopeFromPackage("dev.protsenko.codeguard.rules..")
            .files
            .forEach { file ->
                assertFalse(
                    scopeFunctions.containsMatchIn(file.text),
                    "${file.name} calls scope.functions() directly — use scope.notSuppressedFunctions(suppressKey) instead",
                )
            }
    }

    @Test
    fun `rule files must not call scope properties() directly — use notSuppressedProperties()`() {
        Konsist
            .scopeFromPackage("dev.protsenko.codeguard.rules..")
            .files
            .forEach { file ->
                assertFalse(
                    scopeProperties.containsMatchIn(file.text),
                    "${file.name} calls scope.properties() directly — use scope.notSuppressedProperties(suppressKey) instead",
                )
            }
    }

    @Test
    fun `every rule exposes a non-blank suppressKey with CodeGuard namespace`() {
        val allRules =
            dev.protsenko.codeguard.rules.general.allCoreRules +
                dev.protsenko.codeguard.rules.jpa.allJpaRules +
                dev.protsenko.codeguard.rules.naming.allNamingRules +
                dev.protsenko.codeguard.rules.packages.allPackageRules +
                dev.protsenko.codeguard.rules.web.allWebRules

        allRules.forEach { rule ->
            assert(rule.suppressKey.startsWith("CodeGuard:")) {
                "${rule.description} has invalid suppressKey '${rule.suppressKey}' — must start with 'CodeGuard:'"
            }
            assert(rule.suppressKey.length > "CodeGuard:".length) {
                "${rule.description} has empty suppressKey suffix"
            }
        }
    }
}
