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
            var guard = input.grid.entries.find { (_, value) -> value == '^' }!!.key
            val grid = input.grid

            var direction = Direction.Up
            val visited = mutableSetOf<Point>(guard)

            while (true) {
                val next = guard.nextInDirection(direction)
                if (next in grid.keys) {
                    if (grid[next]!! != '#') {
                        guard = next
                        visited.add(next)
                    } else {
                        direction = direction.nextCW
                    }
                } else {
                    break
                }
            }
            visited.size
        }
        verify {
            expect result 4903
            run test 1 expect 41
        }

        part2 { input ->
            var guard = input.grid.entries.find { (_, value) -> value == '^' }!!.key
            val grid = input.grid

            val available = grid.filter { (key, value) -> key != guard && value == '.' }
            available.count { entry ->
                val newGrid = grid.mapValues { (key, value) ->
                    if (key == entry.key) '#' else value
                }
                run(guard, newGrid)
            }
        }
        verify {
            expect result 1911
            run test 1 expect 6
        }
    }
}

private fun run(initialGuard: Point, input: Map<Point, Char>): Boolean {
    var guard = initialGuard
    val grid = input

    var direction = Direction.Up
    val visitedWithDirection = mutableSetOf(guard to direction)

    while (true) {
        val next = guard.nextInDirection(direction)
        if (next to direction in visitedWithDirection) {
            return true
        }
        if (next in grid.keys) {
            if (grid[next]!! != '#') {
                guard = next
                visitedWithDirection.add(next to direction)
            } else {
                direction = direction.nextCW
            }
        } else {
            break
        }
    }
    return false
}
