package day14

import common.day
import common.util.Point

// answer #1: 225943500
// answer #2: 6377

fun main() {
    day(n = 14) {
        part1 { input ->
            val (width, height) = input.lines.first().split("x").map(String::toInt)
            val robots = input.lines.drop(1)
            val counts = mutableListOf(0, 0, 0, 0)
            parseRobots(robots)
                .map { (pos, vel) ->
                    Point(
                        x = (pos.x + 100 * (vel.x + width)) % width,
                        y = (pos.y + 100 * (vel.y + height)) % height,
                    )
                }
                .mapNotNull { pos ->
                    when {
                        pos.x == width / 2 || pos.y == height / 2 -> null
                        pos.x < width / 2 -> if (pos.y < height / 2) 0 else 1
                        else -> if (pos.y < height / 2) 2 else 3
                    }
                }.forEach {
                    counts[it]++
                }

            counts.reduce { a, b -> a * b }
        }
        verify {
            expect result 225943500
            run test 1 expect 12
        }

        part2 { input ->
            val (width, height) = input.lines.first().split("x").map(String::toInt)
            var guards = parseRobots(input.lines.drop(1))
            val bruteForcedAnswer = 6377

            guards = guards.map { (pos, vel) ->
                var newX = (pos.x + bruteForcedAnswer * (vel.x + width)) % width
                var newY = (pos.y + bruteForcedAnswer * (vel.y + height)) % height
                Point(newX, newY) to vel
            }

            val points = guards.groupingBy { (pos, _) -> pos }.eachCount()
            for (y in 0..<height) {
                for (x in 0..<width) {
                    print(points[Point(x, y)]?.let { if (it < 9) it else '+' } ?: ' ')
                }
                println()
            }

            bruteForcedAnswer
        }
        verify {
            expect result 6377
        }
    }
}

private fun parseRobots(robots: List<String>): List<Pair<Point, Point>> = robots.map {
    val split = it.split(" ")
    val position = split.first().drop(2).split(",").map(String::toInt)
        .let { (x, y) -> Point(x, y) }
    val velocity = split.last().drop(2).split(",").map(String::toInt)
        .let { (x, y) -> Point(x, y) }
    position to velocity
}
