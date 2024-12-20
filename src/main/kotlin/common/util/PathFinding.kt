package common.util

import java.util.PriorityQueue
import kotlin.sequences.forEach

fun Map<Point, Char>.findShortestPath(
    start: Point,
    end: Point,
    validTiles: Set<Char> = setOf('.'),
): Pair<Int, List<Point>> {
    return findShortestPath(
        start,
        end,
        validTiles,
        defaultValue = null,
    )!!
}

fun Map<Point, Char>.findShortestPath(
    start: Point,
    end: Point,
    validTiles: Set<Char> = setOf('.'),
    defaultValue: Pair<Int, List<Point>>?,
): Pair<Int, List<Point>>? {
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
            .filter { this[it] in validTiles }
            .forEach { n ->
                val distanceToN = distance + 1
                if (distanceToN < distances.getValue(n)) {
                    distances[n] = distanceToN
                    queue += n to path + point
                }
            }
    }

    return defaultValue
}
