package dev.protsenko.codeguard.core

/**
 * Shared helpers for unwrapping a (possibly generic) type-name string into the
 * set of concrete type names it references. Used by rules that must detect a
 * forbidden type appearing directly or nested inside generic wrappers such as
 * `List<Entity>`, `ResponseEntity<Entity>`, or `Page<Entity>`.
 */
private val genericWrappers =
    setOf(
        "ResponseEntity",
        "List",
        "MutableList",
        "Set",
        "MutableSet",
        "Collection",
        "Iterable",
        "Page",
    )

private val rawObjectTypeNames = setOf("Any", "Object")

internal fun normalizeType(typeName: String): String =
    typeName
        .trim()
        .removeSuffix("?")
        .removePrefix("out ")
        .removePrefix("in ")

internal fun splitTopLevelTypeArguments(arguments: String): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var depth = 0

    arguments.forEach { char ->
        when (char) {
            '<' -> {
                depth++
                current.append(char)
            }

            '>' -> {
                depth--
                current.append(char)
            }

            ',' if depth == 0 -> {
                result.add(current.toString().trim())
                current.clear()
            }

            else -> {
                current.append(char)
            }
        }
    }

    val tail = current.toString().trim()
    if (tail.isNotEmpty()) {
        result.add(tail)
    }

    return result
}

internal fun extractTypeCandidates(typeName: String): LinkedHashSet<String> {
    val normalized = normalizeType(typeName)
    val genericStart = normalized.indexOf('<')
    return when {
        normalized.isEmpty() || normalized == "*" -> linkedSetOf()
        genericStart < 0 || !normalized.endsWith(">") ->
            linkedSetOf(normalized.substringAfterLast("."))
        else -> {
            val rawType = normalized.substringBefore("<").substringAfterLast(".")
            val inner = normalized.substring(genericStart + 1, normalized.length - 1)
            val result = linkedSetOf<String>()
            if (rawType !in genericWrappers) {
                result.add(rawType)
            }

            splitTopLevelTypeArguments(inner).forEach { argument ->
                result.addAll(extractTypeCandidates(argument))
            }
            result
        }
    }
}

/**
 * True if [typeName] is a raw `Object`/`Any`, directly or as the single element
 * of a collection-style generic (`List<Any>`, `Optional<Object>`, `Page<Any>`).
 * Multi-argument generics such as `Map<String, Any>` return false — the raw type
 * there is a value parameter, not the result type itself.
 */
internal fun isObjectOrAnyType(typeName: String): Boolean {
    val normalized = normalizeType(typeName)
    val genericStart = normalized.indexOf('<')
    if (genericStart < 0 || !normalized.endsWith(">")) {
        return normalized.substringAfterLast(".") in rawObjectTypeNames
    }
    val arguments =
        splitTopLevelTypeArguments(
            normalized.substring(genericStart + 1, normalized.length - 1),
        )
    return arguments.size == 1 && isObjectOrAnyType(arguments[0])
}
