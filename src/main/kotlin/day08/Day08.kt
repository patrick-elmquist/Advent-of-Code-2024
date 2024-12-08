package day08

import common.day
import common.grid
import common.util.Point

// answer #1: 308
// answer #2: 1147

fun main() {
    day(n = 8) {
        part1 { input ->
            val grid = input.grid
            val antennas = parseAntennaPositionsByType(grid)

            val antinodes = mutableSetOf<Point>()
            for (positions in antennas) {
                for (p1 in positions) {
                    for (p2 in positions) {
                        if (p1 == p2) continue

                        val distanceToNext = p1 - p2
                        antinodes += p1 + distanceToNext
                        antinodes += p2 - distanceToNext
                    }
                }
            }

            antinodes.count { it in grid }
        }
        verify {
            expect result 308
            run test 1 expect 14
        }

        part2 { input ->
            val grid = input.grid
            val antennas = parseAntennaPositionsByType(grid)

            val antinodes = mutableSetOf<Point>()
            for (positions in antennas) {
                for (p1 in positions) {
                    for (p2 in positions) {
                        if (p1 == p2) continue

                        val distanceToNext = p1 - p2
                        var next = p1 + distanceToNext
                        while (next in grid) {
                            antinodes.add(next)
                            next += distanceToNext
                        }

                        next = p1 - distanceToNext
                        while (next in grid) {
                            antinodes.add(next)
                            next -= distanceToNext
                        }
                    }
                }
            }

            antinodes.count { it in grid }
        }
        verify {
            expect result 1147
            run test 1 expect 34
        }
    }
}

private fun parseAntennaPositionsByType(grid: Map<Point, Char>): List<List<Point>> =
    grid.filter { it.value != '.' }
        .entries
        .groupBy { (_, value) -> value }
        .map { (_, value) -> value.map { it.key } }
