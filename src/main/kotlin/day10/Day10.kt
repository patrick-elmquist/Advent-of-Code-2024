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
            zeroes.sumOf { findTrails(it, map).toSet().size }
        }
        verify {
            expect result 557
            run test 1 expect 4
        }

        part2 { input ->
            val map = parseMap(input)
            val zeroes = map.filter { (_, value) -> value == 0 }.keys
            zeroes.sumOf { findTrails(it, map).size }
        }
        verify {
            expect result 1062
            run test 1 expect 13
        }
    }
}

private fun parseMap(input: Input): Map<Point, Int> =
    input.grid
        .filterValues { it != '.' }
        .mapValues { it.value.digitToInt() }

private fun findTrails(zero: Point, map: Map<Point, Int>): List<Point> =
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

