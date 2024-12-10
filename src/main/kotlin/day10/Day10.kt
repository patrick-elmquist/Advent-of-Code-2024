package day10

import common.Input
import common.day
import common.grid
import common.util.Point
import common.util.arrayDequeOf
import common.util.neighbors

// answer #1: 557
// answer #2: 1062

fun main() {
    day(n = 10) {
        part1 { input ->
            val map = parseMap(input)
            val zeroes = map.filter { (_, value) -> value == 0 }.keys
            zeroes.sumOf { followTrail(it, map).toSet().size }
        }
        verify {
            expect result 557
            run test 1 expect 1
            run test 2 expect 2
            run test 3 expect 4
            run test 4 expect 3
            run test 5 expect 36
        }

        part2 { input ->
            val map = parseMap(input)
            val zeroes = map.filter { (_, value) -> value == 0 }.keys
            zeroes.sumOf { followTrail(it, map).size }
        }
        verify {
            expect result null
            run test 6 expect 3
            run test 3 expect 13
        }
    }
}

private fun parseMap(input: Input): Map<Point, Int> =
    input.grid
        .filterValues { it != '.' }
        .mapValues { it.value.digitToInt() }

private fun followTrail(zero: Point, map: Map<Point, Int>): List<Point> =
    buildList {
        val queue = arrayDequeOf(zero)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val value = map.getValue(current)
            if (value == 9) {
                add(current)
            } else {
                queue.addAll(current.neighbors().filter { map[it] == value + 1 })
            }
        }
    }

