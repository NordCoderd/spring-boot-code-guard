package dev.protsenko.codeguard.core

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Shared PSI factory for rules that need to inspect Kotlin syntax beyond what Konsist exposes.
 */
internal val kotlinPsiFactory by lazy {
    val env = KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(), CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES,
    )
    KtPsiFactory(env.project, markGenerated = false)
}
