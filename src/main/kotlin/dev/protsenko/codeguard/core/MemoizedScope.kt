package dev.protsenko.codeguard.core

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.declaration.combined.KoClassAndInterfaceDeclaration

/**
 * Wraps a scope for the duration of a single verification run so that the (expensive) declaration
 * collection and the @Suppress lookup are computed once and shared by every rule.
 */
internal class MemoizedScope(private val delegate: KoScope) : KoScope by delegate {
    private val classes = HashMap<Pair<Boolean, Boolean>, List<KoClassDeclaration>>()
    private val interfaces = HashMap<Boolean, List<KoInterfaceDeclaration>>()
    private val classesAndInterfaces = HashMap<Pair<Boolean, Boolean>, List<KoClassAndInterfaceDeclaration>>()
    private val functions = HashMap<Pair<Boolean, Boolean>, List<KoFunctionDeclaration>>()
    private val properties = HashMap<Boolean, List<KoPropertyDeclaration>>()
    private val suppressed = HashMap<String, Set<String>>()

    @Synchronized
    override fun classes(includeNested: Boolean, includeLocal: Boolean) =
        classes.getOrPut(includeNested to includeLocal) { delegate.classes(includeNested, includeLocal) }

    @Synchronized
    override fun interfaces(includeNested: Boolean) =
        interfaces.getOrPut(includeNested) { delegate.interfaces(includeNested) }

    @Synchronized
    override fun classesAndInterfaces(includeNested: Boolean, includeLocal: Boolean) =
        classesAndInterfaces.getOrPut(includeNested to includeLocal) {
            delegate.classesAndInterfaces(includeNested, includeLocal)
        }

    @Synchronized
    override fun functions(includeNested: Boolean, includeLocal: Boolean) =
        functions.getOrPut(includeNested to includeLocal) { delegate.functions(includeNested, includeLocal) }

    @Synchronized
    override fun properties(includeNested: Boolean) =
        properties.getOrPut(includeNested) { delegate.properties(includeNested) }

    @Synchronized
    fun suppressedNames(key: String, compute: () -> Set<String>) = suppressed.getOrPut(key, compute)
}

internal fun KoScope.memoized(): KoScope = if (this is MemoizedScope) this else MemoizedScope(this)
