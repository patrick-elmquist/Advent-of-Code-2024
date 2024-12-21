package day21

import common.day
import common.util.Point
import common.util.flipKeyValue
import common.util.gridOf

// answer #1: 184716
// answer #2: 229403562787554

private val keypadNumeric = gridOf("789", "456", "123", " 0A").flipKeyValue()
private val keypadDirection = gridOf(" ^A", "<v>").flipKeyValue()

fun main() {
    day(n = 21) {
        part1 { input ->
            input.lines.sumOf { code ->
                val n = code.dropLast(1).toLong()
                n * rec(code, Type.Keypad)
                    .flatMap { rec(it, Type.Direction) }
                    .groupBy { it.length }
                    .minBy { it.key }
                    .value
                    .flatMap { rec(it, Type.Direction) }
                    .minOf { it.length.toLong() }
            }
        }
        verify {
            expect result 184716L
            run test 1 expect 126384L
        }

        part2 { input ->
            input.lines.sumOf { code ->
                solve(code, depth = 1, maxDepth = 27) * code.dropLast(1).toLong()
            }
        }
        verify {
            expect result 229403562787554L
        }
    }
}

private enum class Type { Keypad, Direction }

private fun rec(
    code: String,
    type: Type,
    path: String = "",
    position: Char = 'A',
): List<String> {
    val keypad = when (type) {
        Type.Keypad -> keypadNumeric
        Type.Direction -> keypadDirection
    }
    val next = code.firstOrNull() ?: return listOf(path)
    return keypad.shortestPathsBetweenKeys(position, next)
        .flatMap {
            rec(
                code = code.drop(1),
                type = type,
                position = next,
                path = path + it,
            )
        }
}

private fun Map<Char, Point>.shortestPathsBetweenKeys(
    key1: Char,
    key2: Char,
): List<String> {
    val (x1, y1) = getValue(key1)
    val (x2, y2) = getValue(key2)
    val gap = getValue(' ')

    val dx = x2 - x1
    val xMoves = if (dx >= 0) ">".repeat(dx) else "<".repeat(-dx)

    val dy = y2 - y1
    val yMoves = if (dy >= 0) "v".repeat(dy) else "^".repeat(-dy)

    return when {
        dx == 0 && dy == 0 -> listOf("")
        dy == 0 -> listOf(xMoves)
        dx == 0 -> listOf(yMoves)
        gap == Point(x2, y1) -> listOf(yMoves + xMoves)
        gap == Point(x1, y2) -> listOf(xMoves + yMoves)
        else -> listOf(yMoves + xMoves, xMoves + yMoves)
    }.map { it + 'A' }
}

private val cache = mutableMapOf<Pair<String, Int>, Long>()
private fun solve(seq: String, depth: Int, maxDepth: Int): Long =
    cache.getOrPut(seq to depth) {
        if (depth == maxDepth) return@getOrPut seq.length.toLong()

        val keypad = if (depth == 1) keypadNumeric else keypadDirection

        ("A$seq").zipWithNext()
            .map { (a, b) -> keypad.shortestPathsBetweenKeys(a, b) }
            .sumOf { shortestPaths ->
                shortestPaths.minOf { sp -> solve(sp, depth + 1, maxDepth) }
            }
    }
