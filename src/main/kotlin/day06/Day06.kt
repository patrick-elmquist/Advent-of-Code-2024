package day06

import common.day
import common.grid
import common.util.Direction
import common.util.Point
import common.util.nextCW
import common.util.nextInDirection

// answer #1: 4903
// answer #2: 1911

fun main() {
    day(n = 6) {
        part1 { input ->
            val grid = input.grid
            var guardStart = grid.entries.first { it.value == '^' }.key
            calculatePointsOnPath(guardStart, grid).size
        }
        verify {
            expect result 4903
            run test 1 expect 41
        }

        part2 { input ->
            val grid = input.grid.toMutableMap()
            var guardStart = grid.entries.first { it.value == '^' }.key
            // no point in placing an obstacle where the guard do not walk...
            (calculatePointsOnPath(guardStart, grid) - guardStart)
                .count { point ->
                    grid[point] = '#'
                    val hasLoop = lookForLoop(guardStart, grid)
                    grid[point] = '.'
                    hasLoop
                }
        }
        verify {
            expect result 1911
            run test 1 expect 6
        }
    }
}

private fun calculatePointsOnPath(
    guard: Point,
    grid: Map<Point, Char>,
): Set<Point> {
    var guard1 = guard
    var direction = Direction.Up
    val visited = mutableSetOf(guard1)
    while (true) {
        val next = guard1.nextInDirection(direction)
        when (grid[next]) {
            null -> break
            '#' -> direction = direction.nextCW
            else -> {
                guard1 = next
                visited.add(next)
            }
        }
    }
    return visited
}

private fun lookForLoop(
    initialGuard: Point,
    grid: Map<Point, Char>,
): Boolean {
    var guard = initialGuard
    var direction = Direction.Up
    val seenStates = mutableSetOf(guard to direction)
    while (true) {
        var next = guard.nextInDirection(direction)
        val state = next to direction
        if (state in seenStates) return true
        when (grid[next]) {
            null -> return false
            '#' -> direction = direction.nextCW
            else -> {
                guard = next
                seenStates += state
            }
        }
    }
    return false
}
