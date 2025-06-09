package io.github.skydynamic.maiproberplus.core.utils

inline fun <T> compareBy(
    crossinline selector: (T) -> Comparable<*>?,
    vararg selectors: (T) -> Comparable<*>?
): Comparator<T> {
    var comparator = kotlin.comparisons.compareBy(selector)
    for (s in selectors) {
        comparator = comparator.thenComparing(
            kotlin.comparisons.compareBy(s)
        )
    }
    return comparator
}

inline fun <T> compareByDescending(
    crossinline selector: (T) -> Comparable<*>?,
    vararg selectors: (T) -> Comparable<*>?
): Comparator<T> {
    var comparator = kotlin.comparisons.compareByDescending(selector)
    for (s in selectors) {
        comparator = comparator.thenComparing(
            kotlin.comparisons.compareByDescending(s)
        )
    }
    return comparator
}
