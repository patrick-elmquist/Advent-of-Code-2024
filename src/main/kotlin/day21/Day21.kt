package day21

import common.day
import common.util.Point
import common.util.neighbors
import java.util.PriorityQueue

// answer #1: 184716
// answer #2:

private val keyToPointMap = mapOf(
    '7' to Point(0, 0),
    '8' to Point(1, 0),
    '9' to Point(2, 0),
    '4' to Point(0, 1),
    '5' to Point(1, 1),
    '6' to Point(2, 1),
    '1' to Point(0, 2),
    '2' to Point(1, 2),
    '3' to Point(2, 2),
    '0' to Point(1, 3),
    'A' to Point(2, 3),
)
private val pointToKeyMap = keyToPointMap.map { (key, value) -> value to key }.toMap()
private val dirToPointMap = mapOf(
    '^' to Point(1, 0),
    'A' to Point(2, 0),
    '<' to Point(0, 1),
    'v' to Point(1, 1),
    '>' to Point(2, 1),
)
private val pointToDirMap = dirToPointMap.map { (key, value) -> value to key }.toMap()

fun main() {
    day(n = 21) {
        part1 { input ->
            val outputs = input.lines.map { code ->
                val keypadRec = rec(
                    code = code,
                    toCharMap = pointToKeyMap,
                    toPointMap = keyToPointMap,
                )
                val firstRec = keypadRec.flatMap {
                    rec(
                        code = it,
                        toCharMap = pointToDirMap,
                        toPointMap = dirToPointMap,
                    )
                }
                val secondRec = firstRec.flatMap {
                    rec(
                        code = it,
                        toCharMap = pointToDirMap,
                        toPointMap = dirToPointMap,
                    )
                }

                val min = secondRec.minOf { it.length }
                code.dropLast(1).toInt() * min
            }

            outputs.sum()
        }
        verify {
            expect result 184716
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

private fun rec(
    code: String,
    toPointMap: Map<Char, Point>,
    toCharMap: Map<Point, Char>,
    position: Point = toPointMap.getValue('A'),
    path: String = "",
): List<String> {
    if (code.isEmpty()) return listOf(path)

    val c = code[0]
    val end = toPointMap.getValue(c)

    return findAllShortestPath(
        map = toCharMap,
        start = position,
        end = end,
    ).map { p ->
        buildString {
            append(path)
            p.windowed(2, 1, false).forEach { (a, b) ->
                val instruction = when {
                    b.x < a.x -> '<'
                    b.x > a.x -> '>'
                    b.y < a.y -> '^'
                    b.y > a.y -> 'v'
                    else -> error("a:$a b:$b")
                }
//                "checking a:$a b:$b out:$instruction".log()
                append(instruction)
            }

            append('A')
        }
    }.flatMap {
        rec(
            code = code.drop(1),
            position = end,
            path = it,
            toCharMap = toCharMap,
            toPointMap = toPointMap,
        )
    }
}

private val keyCache = mutableMapOf<Pair<Point, Point>, List<List<Point>>>()
private val dirCache = mutableMapOf<Pair<Point, Point>, List<List<Point>>>()
private fun findAllShortestPath(
    map: Map<Point, Char>,
    start: Point,
    end: Point,
): List<List<Point>> {
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
        return@getOrPut paths
    }
}
