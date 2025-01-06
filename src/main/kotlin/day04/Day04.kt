package day04

import common.day
import common.util.Point
import common.util.grid

// answer #1: 2654
// answer #2: 1990

fun main() {
    day(n = 4) {
        part1 { input ->
            val grid = input.lines.grid
            val pattern = "XMAS"
            grid.keys.sumOf { point ->
                listOf(
                    verticalPoints,
                    horizontalPoints,
                    diagonalLeftPoints,
                    diagonalRightPoints,
                ).count { direction -> grid.check(point, direction, pattern) }
            }
        }
        verify {
            expect result 2654
            run test 1 expect 18
        }

        part2 { input ->
            val grid = input.lines.grid
            val pattern = "MAS"
            val centerOffset = Point(1, 1)
            grid.keys.count { point ->
                if (grid[point + centerOffset] == 'A') {
                    val topRightPoint = point.copy(x = point.x + 2)
                    val left = grid.check(topRightPoint, diagonalLeftPoints.take(2), pattern)
                    val right = grid.check(point, diagonalRightPoints.take(2), pattern)
                    left && right
                } else {
                    false
                }
            }
        }
        verify {
            expect result 1990
            run test 1 expect 9
        }
    }
}

private val horizontalPoints = sequenceOf(
    Point(1, 0),
    Point(2, 0),
    Point(3, 0),
)

private val verticalPoints = sequenceOf(
    Point(0, 1),
    Point(0, 2),
    Point(0, 3),
)

private val diagonalLeftPoints = sequenceOf(
    Point(-1, 1),
    Point(-2, 2),
    Point(-3, 3),
)

private val diagonalRightPoints = sequenceOf(
    Point(1, 1),
    Point(2, 2),
    Point(3, 3),
)

private fun Map<Point, Char>.check(
    point: Point,
    directions: Sequence<Point>,
    pattern: String,
): Boolean {
    val c = get(point)
    if (c == pattern.first() || c == pattern.last()) {
        val string = buildString {
            append(c)
            directions
                .map { it + point }
                .forEach { p ->
                    if (p in this@check) {
                        append(getValue(p))
                    } else {
                        return false
                    }
                }
        }
        return string in setOf(pattern, pattern.reversed())
    } else {
        return false
    }
}

