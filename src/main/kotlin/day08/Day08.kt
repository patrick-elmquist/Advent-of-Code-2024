package day08

import common.day
import common.grid
import common.util.Point
import common.util.print

// answer #1: 308
// answer #2: 1147

fun main() {
    day(n = 8) {
        part1 { input ->
            val grid = input.grid
            val antennas = parseAntennasByType(grid)

            val p = mutableSetOf<Point>()
            antennas.map { (_, points) ->
                points.forEach { p1 ->
                    points.filter { it != p1  }
                        .forEach { p2 ->
                            val diffX = Point(p1.x - p2.x, p1.y - p2.y)
                            val diffY = Point(p2.x - p1.x, p2.y - p1.y)
                            p.add(p1 + diffX)
                            p.add(p2 + diffY)
                        }
                }
            }

            p.count { point -> point in grid.keys }
        }
        verify {
            expect result 308
            run test 1 expect 14
        }

        part2 { input ->
            val grid = input.grid
            val antennas = parseAntennasByType(grid)

            val p = mutableSetOf<Point>()
            antennas.map { (_, points) ->
                points.forEach { p1 ->
                    points.filter { it != p1  }
                        .forEach { p2 ->
                            val diffX = Point(p1.x - p2.x, p1.y - p2.y)

                            var newPoint1 = p1 + diffX
                            while (newPoint1 in grid) {
                                p.add(newPoint1)
                                newPoint1 += diffX
                            }
                            var newPoint2 = p1 - diffX
                            while (newPoint2 in grid) {
                                p.add(newPoint2)
                                newPoint2 -= diffX
                            }
                        }
                }
            }

            p.count { point -> point in grid.keys }
        }
        verify {
            expect result 1147
            run test 1 expect 34
        }
    }
}

private fun parseAntennasByType(grid: Map<Point, Char>): List<Pair<Char, List<Point>>> = grid
    .filter { it.value != '.' }
    .entries
    .groupBy { entry -> entry.value }
    .map { entry -> entry.key to entry.value.map { entry -> entry.key } }
