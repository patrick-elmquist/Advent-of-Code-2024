@file:Suppress("NOTHING_TO_INLINE", "unused")

package common.util

var loggingEnabled = true

/**
 * Abuse the not operator fun for quick logging
 * If you are reading this, for the love of god, don't use this in production code
 * Example : !"Log this"
 */
inline operator fun String.not() {
    if (loggingEnabled) println(this)
}

inline fun <T> T.out(): T = log()

inline fun <T> T.log(): T {
    if (loggingEnabled) println(this)
    return this
}

inline infix fun <T> T.log(msg: Any): T {
    if (loggingEnabled)  println("$msg $this")
    return this
}

class MermaidBuilder(private val type: String) {
    private val builder = StringBuilder(type).appendLine()
    private val rows = mutableListOf<String>()
    private val indent = "    "
    fun addRow(vararg items: String) {
        val line = items.joinToString(separator = " --> ", prefix = indent)
        rows += line
    }

    fun build(): String {
        return buildString {
            appendLine(type)
            rows.sorted().forEach { appendLine(it) }
        }
    }

    override fun toString(): String = build()
}

fun printMermaidGraph(type: String = "stateDiagram-v2", block: MermaidBuilder.() -> Unit) {
    val scope = MermaidBuilder(type)
    scope.apply(block)
    scope.build().log()
}

fun <T> Map<Point, T>.print(
    width: IntRange? = null,
    height: IntRange? = null,
    block: (Point, T?) -> Any? = { _, c -> c },
) {
    val xRange = width ?: minMax { it.key.x }
    val yRange = height ?: minMax { it.key.y }
    for (y in yRange) {
        for (x in xRange) {
            val point = Point(x, y)
            if (loggingEnabled) {
                print(block(point, get(point)))
            }
        }
        if (loggingEnabled) {
            println()
        }
    }
}

fun <T> Map<Point, T>.printPadded(
    width: IntRange? = null,
    height: IntRange? = null,
    block: (Point, T?) -> String,
) {
    val xRange = width ?: minMax { it.key.x }
    val yRange = height ?: minMax { it.key.y }
    print("    ")
    for (x in xRange) {
        print(" $x ".padEnd(6))
    }
    println()
    for (y in yRange) {
        print(" $y:".padStart(4))
        for (x in xRange) {
            val point = Point(x, y)
            if (loggingEnabled) {
                print(block(point, get(point)))
            }
        }
        if (loggingEnabled) {
            println()
        }
    }
}

