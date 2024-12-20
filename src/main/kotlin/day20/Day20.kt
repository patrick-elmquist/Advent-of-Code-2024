package day20

import common.Input
import common.day
import common.util.Point
import common.util.distance
import common.util.grid
import common.util.neighbors
import java.util.PriorityQueue
import kotlin.math.min
import kotlin.sequences.forEach

// answer #1: 1452
// answer #2: 999556

private val validTiles = setOf('.', 'S', 'E')
private val cheatTiles = validTiles + '#'
fun main() {
    day(n = 20) {
        part1 { input ->
            val grid = createGrid(input)

            val start = grid.entries.first { it.value == 'S' }.key
            val end = grid.entries.first { it.value == 'E' }.key

            val (shortest, distances) = grid.bfs(start = end, end = start)
            val count = grid.bfs2(
                start = start,
                end = end,
                distanceToEnd = distances,
                limit = shortest,
            )

            count.filter { shortest - it.key >= 100 }
                .values
                .sum()
        }
        verify {
            expect result 1452
            run test 1 expect 0
        }

        part2 { input ->
            val grid = createGrid(input)
            val threshold = if (input.lines.first().length > 20) 100 else 50
            val start = grid.entries.first { it.value == 'S' }.key
            val end = grid.entries.first { it.value == 'E' }.key

            val diamond = buildList {
                for (y in -20..20) {
                    for (x in -20..20) {
                        val p = Point(x, y)
                        if (p != Point.Zero && p.distance(Point.Zero) <= 20) {
                            add(p)
                        }
                    }
                }
            }

            val (shortest, distances) = grid.bfs(start = end, end = start)

            var counter = mutableMapOf<Int, Int>()
            distances.toList().sortedByDescending { it.second }
                .forEachIndexed { i, (point, _) ->
                    diamond.map { it + point }
                        .filter { grid[it] in validTiles }
                        .forEach { after ->
                            val distanceToEnd = distances.getValue(after)
                            val total = i + distanceToEnd + after.distance(point)
                            if (shortest - total >= threshold) {
                                counter.merge(shortest - total, 1, Int::plus)
                            }
                        }
                }
            counter.values.sum()
        }
        verify {
            expect result 999556
            run test 1 expect 285
        }
    }
}

private fun createGrid(input: Input): Map<Point, Char> {
    val width = input.lines.first().length
    val height = input.lines.size
    val grid = input.lines.grid
        .filter { (key, _) -> key.x in 1..width - 2 }
        .filter { (key, _) -> key.y in 1..height - 2 }
    return grid
}


data class State(
    val point: Point,
    val steps: Int,
    val canCheat: Boolean,
    val visited: Set<Point>,
)

private fun Map<Point, Char>.bfs2(
    start: Point,
    end: Point,
    limit: Int,
    distanceToEnd: Map<Point, Int>,
): MutableMap<Int, Int> {
    val count = mutableMapOf<Int, Int>()

    val queue = PriorityQueue<State>(compareBy { it.steps })
    queue.add(State(start, 0, true, emptySet()))

    while (queue.isNotEmpty()) {
        val (point, steps, canCheat, visited) = queue.poll()

        if (steps > limit) continue

        if (point == end) {
            count.merge(steps, 1, Int::plus)
            continue
        }

        if (!canCheat && point in distanceToEnd) {
            val total = steps + distanceToEnd.getValue(point)
            count.merge(total, 1, Int::plus)
            continue
        }

        if (point in visited) continue
        val newVisited = visited + point

        point.neighbors()
            .filter { this[it] in if (canCheat) cheatTiles else validTiles }
            .forEach { n ->
                val distanceToN = steps + 1
                queue += if (this[n] == '#') {
                    State(n, distanceToN, false, newVisited)
                } else {
                    State(n, distanceToN, canCheat, newVisited)
                }
            }
    }

    return count
}

private fun Map<Point, Char>.bfs(start: Point, end: Point): Pair<Int, Map<Point, Int>> {
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    distances[start] = 0

    val queue = PriorityQueue<Point>(compareBy { distances[it] })
    queue.add(start)

    var min = Int.MAX_VALUE
    val visited = mutableSetOf<Point>()
    while (queue.isNotEmpty()) {
        val point = queue.poll()
        val distance = distances.getValue(point)

        if (point == end) {
            min = min(min, distance)
        }

        if (point in visited) continue
        visited += point

        point.neighbors()
            .filter { this[it] in validTiles }
            .forEach { n ->
                val distanceToN = distance + 1
                if (distanceToN < distances.getValue(n)) {
                    distances[n] = distanceToN
                    queue += n
                }
            }
    }

//    distances.printPadded { _, c -> (c ?: ' ').toString().padStart(6) }
    return min to distances.toMap()
}
