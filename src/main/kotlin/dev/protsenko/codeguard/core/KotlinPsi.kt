package dev.protsenko.codeguard.core

import org.jetbrains.kotlin.CoreEnvironmentDeprecation
import org.jetbrains.kotlin.cli.extensionsStorage
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Shared PSI factory for rules that need to inspect Kotlin syntax beyond what Konsist exposes.
 */
@OptIn(CoreEnvironmentDeprecation::class, CompilerConfiguration.Internals::class, ExperimentalCompilerApi::class)
internal val kotlinPsiFactory by lazy {
    val configuration = CompilerConfiguration().apply {
        extensionsStorage = CompilerPluginRegistrar.ExtensionStorage()
    }
    val env = KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(), configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES,
    )
    KtPsiFactory(env.project, markGenerated = false)
}
