package day16

import common.day
import common.grid
import common.util.Direction
import common.util.Point
import common.util.nextCCW
import common.util.nextCW
import common.util.nextInDirection
import java.util.PriorityQueue

// answer #1: 103512
// answer #2: 554

fun main() {
    day(n = 16) {
        part1 { input ->
            var shortestPathSteps = Int.MAX_VALUE
            findShortestPaths(
                grid = input.grid.filter { it.value != '#' },
                onEnd = { _, sum ->
                    shortestPathSteps = sum
                    true
                },
            )
            shortestPathSteps
        }
        verify {
            expect result 103512
            run test 1 expect 7036
            run test 2 expect 11048
        }

        part2 { input ->
            var shortestDistance = Int.MAX_VALUE
            val allShortestPathPoints = mutableSetOf<Point>()
            findShortestPaths(
                grid = input.grid.filter { it.value != '#' },
                onEnd = { path, sum ->
                    allShortestPathPoints.addAll(path)
                    if (sum <= shortestDistance) {
                        shortestDistance = sum
                        false
                    } else {
                        true
                    }
                },
            )
            allShortestPathPoints.size
        }

        verify {
            expect result 554
            run test 1 expect 45
            run test 2 expect 64
        }
    }
}

private fun findShortestPaths(grid: Map<Point, Char>, onEnd: (List<Point>, Int) -> Boolean) {
    val start = grid.entries.first { it.value == 'S' }.key
    val end = grid.entries.first { it.value == 'E' }.key
    val queue = PriorityQueue<Triple<List<Point>, Direction, Int>>(
        compareBy { it.third },
    )
    queue.add(Triple(listOf(start), Direction.Right, 0))

    val seen = mutableMapOf<Pair<Point, Direction>, Int>()

    while (queue.isNotEmpty()) {
        val (path, dir, sum) = queue.poll()
        val point = path.last()

        if (point == end) {
            if (onEnd(path, sum)) return
        }

        if ((seen[point to dir] ?: Int.MAX_VALUE) < sum) continue
        seen[point to dir] = sum

        var nextInDirection = point.nextInDirection(dir)
        if (nextInDirection in grid) {
            queue.add(Triple(path + nextInDirection, dir, sum + 1))
        }
        queue.add(Triple(path, dir.nextCW, sum + 1000))
        queue.add(Triple(path, dir.nextCCW, sum + 1000))
    }

    error("Should never end up here")
}

