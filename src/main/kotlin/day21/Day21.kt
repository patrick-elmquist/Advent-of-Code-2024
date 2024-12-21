package day21

import common.day
import common.util.Point
import common.util.grid
import common.util.gridOf
import common.util.ignoreWhitespace
import common.util.log
import common.util.neighbors
import java.util.PriorityQueue
import kotlin.math.abs

// answer #1: 184716
// answer #2: 229403562787554

private val pointToKeyMap = gridOf("789", "456", "123", " 0A").ignoreWhitespace()
private val keyToPointMap = pointToKeyMap.map { it.value to it.key }.toMap()
private val pointToDirMap = gridOf(" ^A", "<v>").ignoreWhitespace()
private val dirToPointMap = pointToDirMap.map { it.value to it.key }.toMap()

private val keypadNumeric = gridOf("789", "456", "123", "X0A")
private val keypadDirection = gridOf("X^A", "<v>")
fun main() {
    day(n = 21) {
        part1 { input ->
//            input.lines.sumOf { code ->
//                code.dropLast(1).toInt() * rec(code, Type.Keypad)
//                    .flatMap { rec(it, Type.Direction) }
//                    .groupBy { it.length }
//                    .minBy { it.key }
//                    .value
//                    .flatMap { rec(it, Type.Direction) }
//                    .minOf { it.length }
//            }
            input.lines.sumOf { code ->
                solve(code, 4) * code.dropLast(1).toLong()
            }
        }
        verify {
            expect result 184716L
            run test 1 expect 126384L
        }

        part2 { input ->
            input.lines.sumOf { code ->
                solve(code, 27) * code.dropLast(1).toLong()
            }
        }
        verify {
            expect result null
        }
    }
}

private fun findPosition(key: Char, keypad: Map<Char, Point>): Point =
    keypad.getValue(key)

private fun buildShortestPathsBetweenKeys(
    key1: Char,
    key2: Char,
    keypad: Map<Char, Point>,
): List<String> {
    val (c1, r1) = findPosition(key1, keypad)
    val (c2, r2) = findPosition(key2, keypad)
    val gap = findPosition('X', keypad)
    val dr = r2 - r1
    val dc = c2 - c1
    val rowMoves = if (dr >= 0) "v".repeat(abs(dr)) else "^".repeat(abs(dr))
    val colMoves = if (dc >= 0) ">".repeat(abs(dc)) else "<".repeat(abs(dc))
    return when {
        dr == 0 && dc == 0 -> return listOf("")
        dr == 0 -> listOf(colMoves)
        dc == 0 -> listOf(rowMoves)
        gap == Point(c2, r1) -> listOf(rowMoves + colMoves)
        gap == Point(c1, r2) -> listOf(colMoves + rowMoves)
        else -> listOf(rowMoves + colMoves, colMoves + rowMoves)
    }
}

private fun buildSequenceOfShortestPaths(
    seq: String,
    keypad: Map<Char, Point>,
): List<List<String>> {
    val res = mutableListOf<List<String>>()
    ("A$seq").zip(seq).forEach { (key1, key2) ->
        res += listOf(buildShortestPathsBetweenKeys(key1, key2, keypad).map { it + "A" })
    }
    return res
}

private val cache = mutableMapOf<Pair<String, Int>, Long>()
private fun solve(seq: String, depth: Int): Long {
    return cache.getOrPut(seq to depth) {
        if (depth == 1) return@getOrPut seq.length.toLong()

        val keypad = if ("0123456789".any { it in seq }) {
            keypadNumeric
        } else {
            keypadDirection
        }.map { it.value to it.key }.toMap()

        var res = 0L
        for (shortestPaths in buildSequenceOfShortestPaths(seq, keypad)) {
            val solved = shortestPaths.map { sp -> solve(sp, depth - 1) }
            res += solved.min()
        }
        res
    }
}

private enum class Type { Keypad, Direction }

private fun rec(
    code: String,
    type: Type,
    path: String = "",
    position: Point = getMaps(type).first.getValue('A'),
): List<String> {
    val (toPointMap, toCharMap) = getMaps(type)

    if (code.isEmpty()) return listOf(path)

    val c = code[0]
    val end = toPointMap.getValue(c)

    return findAllShortestPath(
        map = toCharMap,
        start = position,
        end = end,
    )
        .map { path + it }
        .flatMap {
            rec(
                code = code.drop(1),
                type = type,
                position = end,
                path = it,
            )
        }
}

private fun getMaps(type: Type): Pair<Map<Char, Point>, Map<Point, Char>> = when (type) {
    Type.Keypad -> keyToPointMap to pointToKeyMap
    Type.Direction -> dirToPointMap to pointToDirMap
}

private val keyCache = mutableMapOf<Pair<Point, Point>, List<String>>()
private val dirCache = mutableMapOf<Pair<Point, Point>, List<String>>()
private fun findAllShortestPath(
    map: Map<Point, Char>,
    start: Point,
    end: Point,
): List<String> {
    val cache = if (map === pointToKeyMap) {
        keyCache
    } else if (map === pointToDirMap) {
        dirCache
    } else {
        error("")
    }
    return cache.getOrPut(start to end) {
        val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
        distances[start] = 0

        val queue = PriorityQueue<Pair<Point, List<Point>>>(compareBy { distances[it.first] })
        queue.add(start to emptyList())

        var min = Int.MAX_VALUE
        val paths = mutableListOf<List<Point>>()
        while (queue.isNotEmpty()) {
            val entry = queue.poll()
            val (point, path) = entry
            val distance = distances.getValue(point)

            if (point == end) {
                val fullPath = path + point
                if (fullPath.size <= min) {
                    min = fullPath.size
                    paths.add(fullPath)
                    continue
                } else {
                    break
                }
            }

            point.neighbors()
                .filter { map[it] != null }
                .forEach { n ->
                    val distanceToN = distance + 1
                    if (distanceToN <= distances.getValue(n)) {
                        distances[n] = distanceToN
                        queue += n to path + point
                    }
                }
        }
        return@getOrPut paths.map { p ->
            buildString {
                p.windowed(2, 1, false).forEach { (a, b) ->
                    val instruction = when {
                        b.x < a.x -> '<'
                        b.x > a.x -> '>'
                        b.y < a.y -> '^'
                        b.y > a.y -> 'v'
                        else -> error("a:$a b:$b")
                    }
                    append(instruction)
                }
                append('A')
            }
        }
    }
}
