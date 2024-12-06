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
            var guard = grid.entries.find { (_, value) -> value == '^' }!!.key

            var direction = Direction.Up
            val visited = mutableSetOf<Point>(guard)

            while (true) {
                val next = guard.nextInDirection(direction)
                if (next in grid.keys) {
                    if (grid.getValue(next) != '#') {
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
            val grid = input.grid
            var guard = grid.entries.find { (_, value) -> value == '^' }!!.key

            grid.filterValues { value -> value == '.' }
                .keys
                .count { point ->
                    val newGrid = grid.toMutableMap().apply { put(point, '#') }
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
        val nextValue = grid[next]
        if (nextValue != null) {
            if (nextValue != '#') {
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
