package day12

import common.day
import common.grid
import common.util.Point
import common.util.aboveNeighbour
import common.util.arrayDequeOf
import common.util.belowNeighbour
import common.util.leftNeighbour
import common.util.neighbors
import common.util.rightNeighbour

// answer #1: 1387004
// answer #2: 844198

fun main() {
    day(n = 12) {
        part1 { input ->
            val map = input.grid
            getRegionsAndPerimeters(map)
                .sumOf { (perimeter, region) -> region.size * perimeter }
        }
        verify {
            expect result 1387004
            run test 1 expect 140
            run test 2 expect 1930
        }

        part2 { input ->
            val map = input.grid
            getRegionsAndPerimeters(map)
                .sumOf { (_, region) -> region.size * countSides(region) }
        }
        verify {
            expect result 844198
            run test 1 expect 80
            run test 9 expect 946
        }
    }
}

private fun getRegionsAndPerimeters(map: Map<Point, Char>): List<Pair<Int, Set<Point>>> {
    val queue = ArrayDeque(map.keys)
    val visited = mutableSetOf<Point>()
    val regions = mutableListOf<Pair<Int, Set<Point>>>()

    while (queue.isNotEmpty()) {
        val tile = queue.removeFirst()
        if (tile in visited) continue
        val (perimeter, includes) = floodFill(tile, map)
        regions += perimeter to includes
        visited += includes
    }

    return regions
}

private fun floodFill(point: Point, map: Map<Point, Char>): Pair<Int, Set<Point>> {
    val queue = arrayDequeOf(point)
    val visited = mutableSetOf<Point>()
    var perimeter = 0
    while (queue.isNotEmpty()) {
        val tile = queue.removeFirst()

        if (tile in visited) continue
        visited += tile

        val value = map.getValue(tile)
        val neighbors = tile.neighbors()
        perimeter += neighbors.count { map[it] != value }
        queue.addAll(neighbors.filter { map[it] == value }.filter { it !in visited })
    }
    return perimeter to visited
}

private fun countSides(region: Set<Point>): Int {
    var top = mutableListOf<Point>()
    var bottom = mutableListOf<Point>()
    var start = mutableListOf<Point>()
    var end = mutableListOf<Point>()

    region.forEach { point ->
        val above = point.aboveNeighbour
        if (above !in region) top += above
        val below = point.belowNeighbour
        if (below !in region) bottom += below
        val left = point.leftNeighbour
        if (left !in region) start += left
        val right = point.rightNeighbour
        if (right !in region) end += right
    }

    var sides = 0
    sides += countSides(top, groupBy = Point::y, checkValue = Point::x)
    sides += countSides(bottom, groupBy = Point::y, checkValue = Point::x)
    sides += countSides(start, groupBy = Point::x, checkValue = Point::y)
    sides += countSides(end, groupBy = Point::x, checkValue = Point::y)
    return sides
}

private fun countSides(
    points: List<Point>,
    groupBy: (Point) -> Int,
    checkValue: (Point) -> Int,
) = points.groupBy(groupBy)
    .entries
    .map { it.value.map(checkValue).sorted() }
    .sumOf { 1 + it.zipWithNext().count { (a, b) -> a + 1 != b } }

