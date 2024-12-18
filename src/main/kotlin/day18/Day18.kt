package day18

import common.Input
import common.day
import common.util.Point
import common.util.neighbors
import common.util.sliceByBlank
import java.util.PriorityQueue

// answer #1: 314
// answer #2: 15,20

fun main() {
    day(n = 18) {
        part1 { input ->
            val (size, count, bytes) = parseInput(input)
            val fallenBytes = bytes.take(count)
            val grid = createGrid(size) { if (it in fallenBytes) '#' else '.' }
            grid.findShortestPath(start = Point(0, 0), end = Point(size, size))
        }
        verify {
            expect result 314
            run test 1 expect 22
        }

        part2 { input ->
            val (size, _, bytes) = parseInput(input)
            val (start, end) = Point(0, 0) to Point(size, size)
            var (min, max) = 0 to bytes.size

            while (true) {
                if (max - min <= 1) break

                val pivot = min + (max - min) / 2

                val fallenBytes = bytes.take(pivot).toSet()
                val grid = createGrid(size) { if (it in fallenBytes) '#' else '.' }

                if (grid.findShortestPath(start, end) == null) {
                    max = pivot
                } else {
                    min = pivot
                }
            }

            bytes[min].let { (x, y) -> "$x,$y" }
        }
        verify {
            expect result "15,20"
            run test 1 expect "6,1"
        }
    }
}

private fun Map<Point, Char>.findShortestPath(start: Point, end: Point): Int? {
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
            .filter { this[it] == '.' }
            .forEach { n ->
                val distanceToN = distance + 1
                if (distanceToN < distances.getValue(n)) {
                    distances[n] = distanceToN
                    queue += n
                }
            }
    }

    return null
}

private fun createGrid(size: Int, transform: (Point) -> Char): Map<Point, Char> =
    buildMap {
        for (y in 0..size) {
            for (x in 0..size) {
                Point(x, y).let { put(it, transform(it)) }
            }
        }
    }

private fun parseInput(input: Input): Triple<Int, Int, List<Point>> {
    val (top, bottom) = input.lines.sliceByBlank()
    val (size, count) = top.first().split(',').map(String::toInt)
    val bytes = bottom.map { it.split(',').map(String::toInt).let(::Point) }
    return Triple(size, count, bytes)
}
