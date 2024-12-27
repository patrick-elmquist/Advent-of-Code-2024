package common.util

/**
 * Expose the 6th component for a list
 */
operator fun <T> List<T>.component6(): T {
    return this[5]
}

/**
 * Create a ArrayDeque of the given elements or an empty if none provided
 */
fun <T> arrayDequeOf(vararg values: T): ArrayDeque<T> =
    ArrayDeque<T>().apply { addAll(values) }

/**
 * Create a Point,Char grid from the given Strings
 */
val Iterable<String>.grid: Map<Point, Char>
    get() = flatMapIndexed { y, row -> row.mapIndexed { x, c -> Point(x, y) to c } }.toMap()

fun Map<Point, Char>.ignoreWhitespace() = filterValues { it != ' ' }

fun Map<Point, Char>.flipKeyValue() = map { it.value to it.key }.toMap()

fun gridOf(vararg strings: String, trimWhiteSpace: Boolean = false): Map<Point, Char> {
    val grid = strings.toList().grid
    return if (trimWhiteSpace) {
        grid.filterValues { it != ' ' }
    } else {
        grid
    }
}

fun <T> List<String>.mapWithRegex(
    regex: Regex,
    transform: (MatchResult.Destructured) -> T,
): List<T> =
    this@mapWithRegex.map { line -> transform(regex.find(line)!!.destructured) }

fun List<String>.sliceByBlank() =
    sliceBy(excludeMatch = true) { _, line -> line.isEmpty() }

fun List<String>.sliceBy(
    excludeMatch: Boolean = false,
    breakCondition: (Int, String) -> Boolean,
) = indices.asSequence()
    .filter { i -> breakCondition(i, get(i)) }
    .drop(if (excludeMatch) 0 else 1)
    .plus(size)
    .fold(mutableListOf<List<String>>() to 0) { (list, start), end ->
        list.add(subList(start, end))
        if (excludeMatch) {
            list to end + 1
        } else {
            list to end
        }
    }
    .first
    .toList()

fun <T> Map<Point, T>.minMax(block: (Map.Entry<Point, T>) -> Int): IntRange {
    val max = maxOf { block(it) }
    val min = minOf { block(it) }
    return min..max
}

fun <K, V> unsafeMapOf(vararg pairs: Pair<K, V>): UnsafeMap<K, V> =
    UnsafeMap<K, V>(pairs.toMap().toMutableMap()).apply { putAll(pairs) }

fun <K, V> Map<K,V>.asUnsafe() = unsafeMapOfAll(this)

fun <K, V> unsafeMapOfAll(map: Map<K, V>): UnsafeMap<K, V> =
    UnsafeMap<K, V>(map.toMutableMap()).apply { putAll(map) }

fun <K, V> unsafeMapOfAll(list: List<Pair<K, V>>): UnsafeMap<K, V> =
    UnsafeMap<K, V>(list.toMap().toMutableMap()).apply { putAll(map) }


fun <K, V> defaultingMapOf(vararg pairs: Pair<K, V>, default: (K) -> V): UnsafeMap<K, V> =
    UnsafeMap<K, V>(pairs.toMap().toMutableMap(), default = default).apply { putAll(pairs) }

data class UnsafeMap<K, V>(
    val map: MutableMap<K, V>,
    val default: ((K) -> V)? = null,
) : MutableMap<K, V> by map {
    override fun get(key: K): V =
        if (default == null) {
            map.getValue(key)
        } else {
            val defaultFn = default
            map.getOrElse(key) { defaultFn(key) }
        }
}

fun <T> List<T>.permutations(): List<List<T>> {
    return if (this.size == 1) listOf(this)
    else this.flatMap { i -> (this - i).permutations().map { listOf(i) + it } }
}

fun String.permutations() = this.toList().permutations().map { it.joinToString("") }
