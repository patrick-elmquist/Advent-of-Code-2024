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
            val antennas = parseAntennasByType(grid)

            val antinodes = mutableSetOf<Point>()
            antennas.map { (_, points) ->
                for (p1 in points) {
                    for (p2 in points) {
                        val distanceToNext = p1 - p2
                        if (distanceToNext != Point.Zero) {
                            antinodes += p1 + distanceToNext
                            antinodes += p2 - distanceToNext
                        }
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
            val antennas = parseAntennasByType(grid)

            val antinodes = mutableSetOf<Point>()
            antennas.map { (_, points) ->
                for (p1 in points) {
                    for (p2 in points) {
                        val distanceToNext = p1 - p2
                        if (distanceToNext != Point.Zero) {
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
            }

            antinodes.count { it in grid }
        }
        verify {
            expect result 1147
            run test 1 expect 34
        }
    }
}

private fun parseAntennasByType(grid: Map<Point, Char>): List<Pair<Char, List<Point>>> =
    grid.filter { it.value != '.' }
        .entries
        .groupBy { (_, value) -> value }
        .map { (key, value) -> key to value.map { it.key } }
