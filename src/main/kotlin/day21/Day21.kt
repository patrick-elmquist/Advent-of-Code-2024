package day21

import common.day
import common.util.Point
import common.util.log
import common.util.neighbors
import common.util.out
import common.util.print
import java.util.PriorityQueue
import kotlin.collections.plus
import kotlin.sequences.forEach

// answer #1:
// answer #2:

// Input: 029A
//  Lvl1: <A^A^^>AvvvA
//  Lvl2: v<<A>>^A<A>A<AAv>A^Av<AAA>^A
private val keyToPointMap = mapOf(
    '7' to Point(0,0),
    '8' to Point(1,0),
    '9' to Point(2,0),
    '4' to Point(0,1),
    '5' to Point(1,1),
    '6' to Point(2,1),
    '1' to Point(0,2),
    '2' to Point(1,2),
    '3' to Point(2,2),
    '0' to Point(1,3),
    'A' to Point(2,3),
)
private val pointToKeyMap = keyToPointMap.map { (key, value) -> value to key }.toMap()
private val dirToPointMap = mapOf(
    '^' to Point(1,0),
    'A' to Point(2,0),
    '<' to Point(0,1),
    'v' to Point(1,1),
    '>' to Point(2,1),
)
private val pointToDirMap = dirToPointMap.map { (key, value) -> value to key }.toMap()

fun main() {
    day(n = 21) {
        part1 { input ->

            val paths = pointToKeyMap.findAllShortestPath(
                start = keyToPointMap.getValue('2'),
                end = keyToPointMap.getValue('9'),
            ).log("all paths:")


            paths.map {
                countTurns(it)
            }.log("turns:")


            paths.size.log()

            pointToKeyMap.print { p, c -> c ?: ' '}
            val code = "029A"
            val keypad = resolveKeypad(code)
            val first = resolveDirection(keypad)
            val second = resolveDirection(first)

            keypad.log("keypad: ")
            first.log("first ${first.length}: ")
            second.log("second ${second.length}: ")
            pointToDirMap.print { _, c -> c ?: ' ' }

            val outputs = input.lines.map { code ->
                val keypad = resolveKeypad(code)
                val first = resolveDirection(keypad)
                val second = resolveDirection(first)
                "code:$code lens ${keypad.length} ${first.length} ${second.length}".log()
                code.dropLast(1).toInt() * second.length
            }.log("sums:")

            val expected = listOf(1972, 58800, 12172, 29184, 24256)
            check(outputs == expected) {
                "\nexpected:$expected\n    was: $outputs"
            }


        }
        verify {
            expect result null
            run test 1 expect 126384
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun resolveDirection(movements: String): String = buildString {
    var position = dirToPointMap.getValue('A').log("position")
    movements.forEach { c ->
        val end = dirToPointMap.getValue(c).log("c:$c end:")

        if (position != end) {
            val path = pointToKeyMap.findAllShortestPath(
                start = position,
                end = end,
            ).minBy { countTurns(it) }

            path.log("path (${path.size}):")
            path.windowed(2, 1, false).forEach { (a, b) ->
                val instruction = when {
                    b.x < a.x -> '<'
                    b.x > a.x -> '>'
                    b.y < a.y -> '^'
                    b.y > a.y -> 'v'
                    else -> error("a:$a b:$b")
                }
                "checking a:$a b:$b out:$instruction".log()
                append(instruction)
            }
        }

        append('A')
        position = end
        println()
    }
}

private fun resolveKeypad(code: String): String = buildString {
    var position = keyToPointMap.getValue('A').log("position")
    code.forEach { c ->
        val end = keyToPointMap.getValue(c).log("c:$c end:")

        val path = pointToKeyMap.findAllShortestPath(
            start = position,
            end = end,
        ).minBy { countTurns(it) }

        path.log("path:")
        path.windowed(2, 1, false).forEach { (a, b) ->
            val instruction = when {
                b.x < a.x -> '<'
                b.x > a.x -> '>'
                b.y < a.y -> '^'
                b.y > a.y -> 'v'
                else -> error("a:$a b:$b")
            }
            "checking a:$a b:$b out:$instruction".log()
            append(instruction)
        }

        append('A')
        position = end
        println()
    }
}

private fun Map<Point, Char>.findShortestPath(
    start: Point,
    end: Point,
): Pair<Int, List<Point>> {
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    distances[start] = 0

    val queue = PriorityQueue<Pair<Point, List<Point>>>(compareBy { distances[it.first] })
    queue.add(start to emptyList())

    val visited = mutableSetOf<Point>()
    while (queue.isNotEmpty()) {
        val entry = queue.poll()
        val (point, path) = entry
        val distance = distances.getValue(point)

        if (point == end) return distance to path + point

        if (point in visited) continue
        visited += point

        point.neighbors()
            .filter { this[it] != null }
            .forEach { n ->
                val distanceToN = distance + 1
                if (distanceToN < distances.getValue(n)) {
                    distances[n] = distanceToN
                    queue += n to path + point
                }
            }
    }

    error("")
}

private fun Map<Point, Char>.findAllShortestPath(
    start: Point,
    end: Point,
): List<List<Point>> {
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    distances[start] = 0

    val queue = PriorityQueue<Pair<Point, List<Point>>>(compareBy { distances[it.first] })
    queue.add(start to emptyList())

    var min = Int.MAX_VALUE
    val visited = mutableSetOf<Point>()
    val paths = mutableListOf<List<Point>>()
    while (queue.isNotEmpty()) {
        val entry = queue.poll()
       entry.log("entry:")
        val (point, path) = entry
        val distance = distances.getValue(point)

        if (point == end) {
            val fullPath = path + point
            if (fullPath.size <= min) {
                fullPath.log("PATH:")
                min = fullPath.size
                paths.add(fullPath)
                continue
            } else {
                queue.log("queue:")
                break
            }
        }

//        if (point in visited) {
//            continue
//        }
        visited += point

        point.neighbors()
            .filter { this[it] != null }
            .forEach { n ->
                val distanceToN = distance + 1
                if (distanceToN <= distances.getValue(n)) {
                    "adding n:$n".log()
                    distances[n] = distanceToN
                    queue += n to path + point
                }
            }
    }

    return paths
}

private fun countTurns(points: List<Point>): Int {
    return points.windowed(3, 1, false).count { (a, b, c) ->
        var count = 0
        if (a.x != c.x) count++
        if (a.y != c.y) count++
        count == 2
    }
}

