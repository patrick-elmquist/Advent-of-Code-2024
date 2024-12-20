package day20

import common.day
import common.util.Point
import common.util.grid
import common.util.log
import common.util.neighbors
import common.util.print
import common.util.printPadded
import java.util.PriorityQueue
import kotlin.sequences.forEach

// answer #1:
// answer #2:

fun main() {
    day(n = 20) {
        part1 { input ->
            val width = input.lines.first().length
            val height = input.lines.size
            val grid = input.lines.grid
                .filter { (key, _) -> key.x in 1..width - 2 }
                .filter { (key, _) -> key.y in 1..height - 2 }

//            grid.print()

            val start = grid.entries.first { it.value == 'S' }.key
            val end = grid.entries.first { it.value == 'E' }.key

            val fastestNonCheat = grid.bfs(start, end)
//            check(fastestNonCheat == 84) { "Fastest was $fastestNonCheat" }

            val result = grid.dfs(
                start = start,
                end = end,
                steps = 0,
                limit = fastestNonCheat,
                cheatAllowed = true,
                visited = emptySet(),
            ).groupingBy { it }.eachCount().log("dfs:")


            result
                .mapKeys { fastestNonCheat - it.key }
                .filterKeys { it >= 100 }
                .count()
                .log("adjusted:")
        }
        verify {
            expect result null
            run test 1 expect 0
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private val validTiles = setOf('.', 'S', 'E')
private val cheatTiles = validTiles + '#'

private fun Map<Point, Char>.dfs(
    start: Point,
    end: Point,
    steps: Int,
    limit: Int,
    visited: Set<Point>,
    cheatAllowed: Boolean,
): List<Int> {
    if (steps > limit) return emptyList()
    if (start == end) return listOf(steps)

    val newVisited = visited + start

    return start.neighbors()
        .filter { this[it] in if (cheatAllowed) cheatTiles else validTiles }
        .toList()
        .flatMap { point ->
            if (point !in newVisited) {
                val canCheat = cheatAllowed && this[point] != '#'
                dfs(
                    start = point,
                    end = end,
                    steps = steps + 1,
                    limit = limit,
                    visited = newVisited,
                    cheatAllowed = canCheat
                )
            } else {
                emptyList()
            }
        }
}

private fun Map<Point, Char>.bfs(start: Point, end: Point): Int {
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    distances[start] = 0

    val queue = PriorityQueue<Point>(compareBy { distances[it] })
    queue.add(start)

    val visited = mutableSetOf<Point>()
    while (queue.isNotEmpty()) {
        val point = queue.poll()
        val distance = distances.getValue(point)

        if (point == end) return distance

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

    distances.printPadded { _, c -> (c ?: ' ').toString().padStart(6) }
    error("did not find end")
}
