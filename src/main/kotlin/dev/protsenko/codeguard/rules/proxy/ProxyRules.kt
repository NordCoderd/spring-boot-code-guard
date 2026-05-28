package dev.protsenko.codeguard.rules.proxy

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.rules.SpringAnnotations
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parents

private data class Method(val n: String, val l: String, val a: Int, val ref: Boolean = false)
private typealias Methods = Map<String, List<Method>>

private data class Model(val n: String, val t: String, val m: Methods, val o: Set<String>, val a: Set<String>)

object ProxyRules {
    private val psiFactory by lazy {
        val env = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(), CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES,
        )
        KtPsiFactory(env.project, markGenerated = false)
    }

    val noSelfInvocationOfProxyMethodsRule = object : SpringBootRule {
        override val description =
            "Proxy-annotated methods must not invoke other proxy-annotated methods of the same class"
        override val suppressKey = "CodeGuard:noSelfInvocationOfProxyMethods"
        override fun verify(scope: KoScope) {
            val failures = proxyModels(scope).flatMap { model ->
                psiFactory.createFile(model.t).collectDescendantsOfType<KtClassOrObject>()
                    .firstOrNull { it.name == model.n }?.collectViolations(model) ?: emptyList()
            }
            if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
        }
    }

    private fun KtClassOrObject.collectViolations(model: Model): List<String> {
        val methodViolations = collectDescendantsOfType<KtNamedFunction>()
            .filter { it.name in model.o }
            .mapNotNull { function ->
                function.name?.let { name ->
                    val arity = function.valueParameters.size
                    val label = model.m[name]?.firstOrNull { it.a == arity }?.l
                    function.firstViolationOf(model, name, arity) { methodMessage(model.n, name, label, it) }
                }
            }
        val body = getBody()
        val initViolations = (
            collectDescendantsOfType<KtSecondaryConstructor>().map { it.bodyExpression to "constructor" } +
                body?.anonymousInitializers.orEmpty().map { it.body to "init block" } +
                body?.properties.orEmpty().map { it.initializer to "property '${it.name}' initializer" }
        ).mapNotNull { (element, source) ->
            element.firstViolationOf(model) {
                "${model.n} $source invokes ${it.l} method ${it.n} of the same class — Spring AOP " +
                    "proxy is bypassed during initialization, the annotation will be silently ignored."
            }
        }
        return methodViolations + initViolations
    }

    private fun PsiElement?.firstViolationOf(
        m: Model, n: String? = null, a: Int? = null, msg: (Method) -> String,
    ): String? =
        this?.let { psi ->
            (psi.collectDescendantsOfType<KtCallExpression>().mapNotNull { toHit(it, m, n, a) } +
                    psi.collectDescendantsOfType<KtCallableReferenceExpression>().mapNotNull { toHit(it, m, n) })
                .minByOrNull { it.first.textOffset }?.second
        }?.let { msg(it) }

    private fun toHit(
        expr: KtCallExpression,
        model: Model,
        caller: String?,
        callerArity: Int?,
    ): Pair<PsiElement, Method>? {
        val name = expr.calleeExpression?.text
        val methods = name?.let { model.m[it] }
        if (name == null || methods == null) return null
        val arity = expr.valueArguments.size
        val receiver = (expr.parent as? KtQualifiedExpression)?.receiverExpression
        val excluded = (name == caller && arity == callerArity) ||
            receiver?.isSelf() == false ||
            (receiver == null && (expr.isShadowed(name) || expr.hasForeignImplicitReceiver())) ||
            (name in model.a && methods.none { it.a == arity })
        return if (excluded) null
        else expr to (methods.firstOrNull { it.a == arity } ?: methods.first())
    }

    private fun toHit(
        expr: KtCallableReferenceExpression,
        model: Model,
        caller: String?,
    ): Pair<PsiElement, Method>? {
        val name = expr.callableReference.text
        val methods = model.m[name] ?: return null
        val receiver = expr.children.firstOrNull { it != expr.callableReference }
        val excluded = name == caller ||
            receiver?.isSelf() == false ||
            (receiver == null && expr.isShadowed(name)) ||
            name in model.a
        return if (excluded) null
        else expr to methods.first().copy(ref = true)
    }

    private fun methodMessage(className: String, callerName: String, callerLabel: String?, method: Method): String {
        val caller = callerLabel?.let { "$className.$callerName is annotated with $it" }
            ?: "$className.$callerName"
        val action = if (method.ref) "captures a method reference to" else "invokes"
        val consequence = if (method.ref)
            "invoking the reference bypasses Spring AOP proxy"
        else
            "Spring AOP proxy is bypassed on self-invocation"
        val conjunction = if (callerLabel == null) "" else " and"
        return "$caller$conjunction $action ${method.l} method ${method.n} " +
            "of the same class — $consequence, " +
            "the inner annotation will be silently ignored."
    }

    private fun proxyModels(scope: KoScope): List<Model> {
        val classes = scope.notSuppressedClasses(noSelfInvocationOfProxyMethodsRule.suppressKey)
        val classByName = classes.associateBy { it.name }
        return classes.mapNotNull { klass ->
            val functions = klass.functions()
            val ownMethods = functions.mapNotNull { it.proxyMethod() }
            val ownProxyNames: Set<String> = ownMethods.map { it.n }.toSet()
            val ownNames: Set<String> = functions.map { it.name }.toSet()
            val inherited =
                klass.parents(indirectParents = true).mapNotNull { classByName[it.name] }.flatMap { parent ->
                    parent.functions().mapNotNull { it.proxyMethod() }
                }.filter { it.n !in ownNames || it.n in ownProxyNames }
            val methods = (inherited + ownMethods).groupBy { it.n }.ifEmpty { return@mapNotNull null }
            val ambiguous: Set<String> = methods.keys.filter { name ->
                functions.any { it.name == name && it.proxyMethod() == null }
            }.toSet()
            Model(klass.name, klass.text, methods, ownNames, ambiguous)
        }
    }

    private fun KoFunctionDeclaration.proxyMethod(): Method? = SpringAnnotations.proxyAnnotations
        .firstOrNull { hasAnnotationWithName(it) }
        ?.let { Method(name, "@${it.substringAfterLast(".")}", parameters.size) }

    private fun PsiElement.isSelf(): Boolean {
        tailrec fun PsiElement.unwrapped(): PsiElement =
            (this as? KtParenthesizedExpression)?.expression?.unwrapped() ?: this
        return unwrapped().let { it is KtThisExpression || it is KtSuperExpression }
    }

    private fun PsiElement.hasForeignImplicitReceiver(): Boolean =
        parents.filterIsInstance<KtLambdaExpression>().any { lambda ->
            lambda.getParentOfType<KtCallExpression>(strict = true)
                ?.takeIf { it.calleeExpression?.text in setOf("apply", "run", "with") }
                ?.let { call ->
                    val callee = call.calleeExpression?.text
                    (callee == "with" &&
                        call.valueArguments.firstOrNull()?.getArgumentExpression()?.isSelf() != true) ||
                        (callee != "with" &&
                            (call.parent as? KtDotQualifiedExpression)?.receiverExpression?.isSelf() == false)
                } ?: false
        }

    private fun PsiElement.isShadowed(n: String): Boolean {
        val o = textOffset
        fun KtParameter.matchesName() = name == n || destructuringDeclaration?.entries.orEmpty().any { it.name == n }
        fun PsiElement.declares() =
            this is KtBinaryExpression && left?.text == n || this is KtNamedDeclaration && name == n
        return parents.any { s ->
            when (s) {
                is KtNamedFunction -> s.valueParameters.any { it.name == n }
                is KtLambdaExpression -> s.valueParameters.any { it.matchesName() }
                is KtForExpression -> s.loopParameter?.name == n
                is KtBlockExpression -> s.statements.asSequence().takeWhile { it.textOffset < o }.any { it.declares() }

                else -> false
            }
        }
    }
}

val allProxyRules: List<SpringBootRule> = listOf(ProxyRules.noSelfInvocationOfProxyMethodsRule)
