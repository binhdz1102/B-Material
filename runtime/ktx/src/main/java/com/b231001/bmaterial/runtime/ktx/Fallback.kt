package com.b231001.bmaterial.runtime.ktx

/**
 * Replace the value when it equals [blockedValue]. Otherwise, keep the original.
 * Nulls are preserved (i.e., null won't be replaced unless [blockedValue] is null).
 */
fun <T> T?.fallbackIfEquals(
    blockedValue: T?,
    replacement: T
): T? = if (this == blockedValue) replacement else this

/**
 * Lazy replacement
 */
suspend inline fun <T> T?.fallbackIfEquals(
    blockedValue: T?,
    crossinline replacement: suspend () -> T
): T? = if (this == blockedValue) replacement() else this

/**
 * Replace the value when it equals [blockedValue]. Otherwise, keep the original.
 * Nulls are preserved (i.e., null won't be replaced unless [blockedValue] is null).
 * Blacklist: replace when value is inside [blockedValues].
 * Returns nullable to preserve null semantics.
 */
fun <T> T?.fallbackIfIn(
    blockedValues: Collection<T?>,
    replacement: T
): T? {
    val set = if (blockedValues is Set) blockedValues else blockedValues.toHashSet()
    return if (set.contains(this)) replacement else this
}

fun <T> T?.fallbackIfIn(
    vararg blockedValues: T?,
    replacement: T
): T? = this.fallbackIfIn(blockedValues.asList(), replacement)

/**
 * Lazy replacement
 */
suspend inline fun <T> T?.fallbackIfIn(
    blockedValues: Collection<T?>,
    crossinline replacement: suspend () -> T
): T? {
    val set = if (blockedValues is Set) blockedValues else blockedValues.toHashSet()
    return if (set.contains(this)) replacement() else this
}

/**
 * Whitelist (non-null guarantee):
 * - If receiver is null -> return [replacement]
 * - If receiver NOT in [allowedValues] -> return [replacement]
 * - Else -> return receiver (non-null)
 */
fun <T : Any> T?.fallbackUnlessInOr(
    allowedValues: Collection<T>,
    replacement: T
): T {
    val set = if (allowedValues is Set) allowedValues else allowedValues.toHashSet()
    val v = this
    return if (v != null && set.contains(v)) v else replacement
}

fun <T : Any> T?.fallbackUnlessInOr(
    vararg allowedValues: T,
    replacement: T
): T = fallbackUnlessInOr(allowedValues.asList(), replacement)

/**
 * Lazy replacement
 */
suspend inline fun <T : Any> T?.fallbackUnlessInOr(
    allowedValues: Collection<T>,
    crossinline replacement: suspend () -> T
): T {
    val set = if (allowedValues is Set) allowedValues else allowedValues.toHashSet()
    val v = this
    return if (v != null && set.contains(v)) v else replacement()
}

/**
 * Whitelist that preserves null:
 * - If receiver is in [allowedValues] (including null) -> return receiver (may be null)
 * - Else -> return [replacement]
 */
fun <T> T?.fallbackUnlessInNullable(
    allowedValues: Collection<T?>,
    replacement: T
): T? {
    val set = if (allowedValues is Set) allowedValues else allowedValues.toHashSet()
    return if (set.contains(this)) this else replacement
}

fun <T> T?.fallbackUnlessInNullable(
    vararg allowedValues: T?,
    replacement: T
): T? = fallbackUnlessInNullable(allowedValues.asList(), replacement)

/**
 * Lazy replacement
 */
suspend inline fun <T> T?.fallbackUnlessInNullable(
    allowedValues: Collection<T?>,
    crossinline replacement: suspend () -> T
): T? {
    val set = if (allowedValues is Set) allowedValues else allowedValues.toHashSet()
    return if (set.contains(this)) this else replacement()
}

/**
 * Replace when equals [blockedValue] OR when the value is null.
 * Always returns non-null T by using [replacement] in those cases.
 */
fun <T> T?.fallbackIfNullOrEquals(
    blockedValue: T?,
    replacement: T
): T = if (this == null || this == blockedValue) replacement else this
