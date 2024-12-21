package day21

import common.day
import common.util.Point
import common.util.flipKeyValue
import common.util.gridOf

// answer #1: 184716
// answer #2: 229403562787554

private val numericKeypad = gridOf("789", "456", "123", " 0A").flipKeyValue()
private val directionKeypad = gridOf(" ^A", "<v>").flipKeyValue()

fun main() {
    day(n = 21) {
        part1 { input ->
            input.lines
                .sumOf { code ->
                    val n = code.dropLast(1).toLong()
                    val minLength = numericKeypad.resolve(code)
                        .flatMap { steps -> directionKeypad.resolve(steps) }
                        .flatMap { steps -> directionKeypad.resolve(steps) }
                        .minOf { it.length.toLong() }
                    minLength * n
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

private fun Map<Char, Point>.resolve(
    code: String,
    path: String = "",
    position: Char = 'A',
): List<String> {
    val next = code.firstOrNull() ?: return listOf(path)
    return shortestPathsBetweenKeys(position, next)
        .flatMap {
            resolve(
                code = code.drop(1),
                path = path + it,
                position = next,
            )
        }
}

private val shortestPathCache = mutableMapOf<Pair<Char, Char>, List<String>>()
private fun Map<Char, Point>.shortestPathsBetweenKeys(
    key1: Char,
    key2: Char,
): List<String> = shortestPathCache.getOrPut(key1 to key2) {
    val (x1, y1) = getValue(key1)
    val (x2, y2) = getValue(key2)
    val gap = getValue(' ')

    val dx = x2 - x1
    val xMovement = if (dx >= 0) ">".repeat(dx) else "<".repeat(-dx)

    val dy = y2 - y1
    val yMovement = if (dy >= 0) "v".repeat(dy) else "^".repeat(-dy)

    when {
        dx == 0 && dy == 0 -> listOf("")
        dy == 0 -> listOf(xMovement)
        dx == 0 -> listOf(yMovement)
        gap == Point(x2, y1) -> listOf(yMovement + xMovement)
        gap == Point(x1, y2) -> listOf(xMovement + yMovement)
        else -> listOf(yMovement + xMovement, xMovement + yMovement)
    }.map { it + 'A' }
}

private val sequenceCache = mutableMapOf<Pair<String, Int>, Long>()
private fun solve(sequence: String, depth: Int, maxDepth: Int): Long =
    sequenceCache.getOrPut(sequence to depth) {
        if (depth == maxDepth) {
            sequence.length.toLong()
        } else {
            val keypad = if (depth == 1) numericKeypad else directionKeypad
            ("A$sequence").zipWithNext()
                .map { (a, b) -> keypad.shortestPathsBetweenKeys(a, b) }
                .sumOf { shortestPaths ->
                    shortestPaths.minOf { sp -> solve(sp, depth + 1, maxDepth) }
                }
        }
    }
