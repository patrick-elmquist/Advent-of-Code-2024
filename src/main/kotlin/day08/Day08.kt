package day08

import common.day
import common.grid
import common.util.Point
import common.util.log
import common.util.print

// answer #1: 308
// answer #2: 1147

fun main() {
    day(n = 8) {
        part1 { input ->
            val grid = input.grid
            val antennas = grid
                .filter { it.value != '.' }
                .entries
                .groupBy { entry -> entry.value }
                .map { entry -> entry.key to entry.value.map { entry -> entry.key } }

            println(antennas)

            val p = mutableSetOf<Point>()
            antennas.map { (type, points) ->
                points.forEach { p1 ->
                    points.filter { it != p1  }
                        .forEach { p2 ->
                            val diffX = Point(1 * (p1.x - p2.x), 1 * (p1.y - p2.y))
                            val diffY = Point(1 * (p2.x - p1.x), 1 * (p2.y - p1.y))

                            val newPoint1 = p1 + diffX
                            val newPoint2 = p2 + diffY

                            "p1:$p1 p2:$p2 adding points $newPoint1 $newPoint2".log()
                            p.add(newPoint1)
                            p.add(newPoint2)
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
            val antennas = grid
                .filter { it.value != '.' }
                .entries
                .groupBy { entry -> entry.value }
                .map { entry -> entry.key to entry.value.map { entry -> entry.key } }

            println(antennas)

            val p = mutableSetOf<Point>()
            antennas.map { (type, points) ->
                points.forEach { p1 ->
                    points.filter { it != p1  }
                        .forEach { p2 ->
                            val diffX = Point(1 * (p1.x - p2.x), 1 * (p1.y - p2.y))
                            val diffY = Point(1 * (p2.x - p1.x), 1 * (p2.y - p1.y))

                            var newPoint1 = p1 + diffX
                            while (newPoint1 in grid) {
                                p.add(newPoint1)
                                newPoint1 = newPoint1 + diffX
                            }
                            var newPoint2 = p1 - diffX
                            while (newPoint2 in grid) {
                                p.add(newPoint2)
                                newPoint2 = newPoint2 - diffX
                            }
                        }
                }
            }

            println(p)
            grid.print { t, c -> if (t in p && c == '.') '#' else c }

            p.count { point -> point in grid.keys }
        }
        verify {
//            breakAfterTest()
            expect result 1147
            run test 1 expect 34
        }
    }
}
