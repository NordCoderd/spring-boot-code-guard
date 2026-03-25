package dev.protsenko.codeguard.core

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.declaration.combined.KoClassAndInterfaceDeclaration

private fun suppressedClassNames(
    scope: KoScope,
    suppressKey: String,
): Set<String> =
    scope
        .classes()
        .filter { klass ->
            klass.annotations
                .filter { it.name == "Suppress" }
                .any { annotation ->
                    annotation.arguments.any { arg ->
                        arg.value?.removeSurrounding("\"") == suppressKey
                    }
                }
        }.map { it.name }
        .toSet()

fun KoScope.notSuppressedClasses(suppressKey: String): List<KoClassDeclaration> {
    val suppressed = suppressedClassNames(this, suppressKey)
    return classes().filterNot { it.name in suppressed }
}

fun KoScope.notSuppressedClassesAndInterfaces(suppressKey: String): List<KoClassAndInterfaceDeclaration> {
    val suppressed = suppressedClassNames(this, suppressKey)
    return classesAndInterfaces().filterNot { it.name in suppressed }
}

fun KoScope.notSuppressedFunctions(suppressKey: String): List<KoFunctionDeclaration> {
    val suppressed = suppressedClassNames(this, suppressKey)
    return functions().filterNot { it.containingDeclaration.toString() in suppressed }
}

fun KoScope.notSuppressedProperties(suppressKey: String): List<KoPropertyDeclaration> {
    val suppressed = suppressedClassNames(this, suppressKey)

    return properties()
        .filterNot { it.containingDeclaration.toString() in suppressed }
}
