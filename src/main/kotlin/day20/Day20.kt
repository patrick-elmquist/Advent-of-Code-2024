package day20

import common.day
import common.util.Point
import common.util.distance
import common.util.grid
import common.util.neighbors
import java.util.PriorityQueue

// answer #1: 1452
// answer #2: 999556

fun main() {
    day(n = 20) {
        part1 { input ->
            countCheats(grid = input.lines.grid, maxCheatsAllowed = 2)
                .filterKeys { it >= 100 }
                .values
                .sum()
        }
        verify {
            expect result 1452
            run test 1 expect 0
        }

        part2 { input ->
            val threshold = if (input.lines.first().length > 20) 100 else 50
            countCheats(grid = input.lines.grid, maxCheatsAllowed = 20)
                .filterKeys { it >= threshold }
                .values
                .sum()
        }
        verify {
            expect result 999556
            run test 1 expect 285
        }
    }
}

private val validTiles = setOf('.', 'S', 'E')
private fun countCheats(
    grid: Map<Point, Char>,
    maxCheatsAllowed: Int,
): Map<Int, Int> {
    val distances = grid.calculateDistances()
    val shortest = distances.values.max()
    val searchArea = createSearchArea(radius = maxCheatsAllowed)
    return buildMap {
        distances.entries
            .sortedByDescending { (_, steps) -> steps }
            .forEachIndexed { steps, (point, _) ->
                searchArea.map { it + point }
                    .filter { grid[it] in validTiles }
                    .forEach { destination ->
                        val distanceToEnd = distances.getValue(destination)
                        val total = steps + distanceToEnd + destination.distance(point)
                        merge(shortest - total, 1, Int::plus)
                    }
            }
    }
}

private fun Map<Point, Char>.calculateDistances(): Map<Point, Int> {
    val start = entries.first { it.value == 'S' }.key
    val end = entries.first { it.value == 'E' }.key

    val distances = mutableMapOf<Point, Int>(end to 0)
    val queue = PriorityQueue<Point>(compareBy { distances[it] })
    queue.add(end)

    var previous: Point? = null
    while (queue.isNotEmpty()) {
        val point = queue.poll()

        if (point == start) break

        point.neighbors()
            .first { this[it] in validTiles && it != previous }
            .let { next ->
                distances[next] = distances.getValue(point) + 1
                queue += next
            }

        previous = point
    }

    return distances.toMap()
}

private fun createSearchArea(radius: Int): List<Point> = buildList {
    for (y in -radius..radius) {
        for (x in -radius..radius) {
            val p = Point(x, y)
            if (p != Point.Zero && p.distance(Point.Zero) <= radius) {
                add(p)
            }
        }
    }
}
