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
            grid.keys.sumOf { point ->
                listOf(
                    grid.findVertical(point, "XMAS"),
                    grid.findHorizontal(point, "XMAS"),
                    grid.findDiagonalLeft(point, "XMAS"),
                    grid.findDiagonalRight(point, "XMAS"),
                ).count { matched -> matched }
            }
        }
        verify {
            expect result 2654
            run test 1 expect 18
        }

        part2 { input ->
            val grid = input.lines.grid
            grid.keys.count { point -> grid.findDiagonalX(point, "MAS") }
        }
        verify {
            expect result 1990
            run test 1 expect 9
        }
    }
}

private val horizontalPoints = sequenceOf(Point.ZERO, Point(1, 0), Point(2, 0), Point(3, 0))
private val verticalPoints = sequenceOf(Point.ZERO, Point(0, 1), Point(0, 2), Point(0, 3))
private val diagonalLeftPoints = sequenceOf(Point.ZERO, Point(-1, 1), Point(-2, 2), Point(-3, 3))
private val diagonalRightPoints = sequenceOf(Point.ZERO, Point(1, 1), Point(2, 2), Point(3, 3))

private fun Map<Point, Char>.findVertical(point: Point, pattern: String): Boolean =
    check(verticalPoints.map { it + point }, pattern)

private fun Map<Point, Char>.findHorizontal(point: Point, pattern: String): Boolean =
    check(horizontalPoints.map { it + point }, pattern)

private fun Map<Point, Char>.findDiagonalRight(point: Point, pattern: String): Boolean =
    check(diagonalRightPoints.map { it + point }, pattern)

private fun Map<Point, Char>.findDiagonalLeft(point: Point, pattern: String): Boolean =
    check(diagonalLeftPoints.map { it + point }, pattern)

private fun Map<Point, Char>.findDiagonalX(point: Point, pattern: String): Boolean {
    val right = check(diagonalRightPoints.take(3).map { it + point }, pattern)
    val topRightPoint = point.copy(x = point.x + 2)
    val left = check(diagonalLeftPoints.take(3).map { it + topRightPoint }, pattern)
    return right && left
}

private fun Map<Point, Char>.check(points: Sequence<Point>, pattern: String): Boolean {
    val string = buildString {
        points.forEach { p ->
            if (p !in this@check) return false
            append(getValue(p))
        }
    }
    return string.matches(pattern)
}

private fun String.matches(pattern: String): Boolean =
    length == pattern.length && this in setOf(pattern, pattern.reversed())
