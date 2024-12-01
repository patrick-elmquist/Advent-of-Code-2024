package day01

import common.Input
import common.day
import kotlin.math.abs

// answer #1: 1666427
// answer #2: 24316233

fun main() {
    day(n = 1) {
        part1 { input ->
            val (left, right) = parseToLists(input)
            left.sorted().zip(right.sorted()).sumOf { (a, b) -> abs(a - b) }
        }
        verify {
            expect result 1666427
            run test 1 expect 11
        }

        part2 { input ->
            val (left, right) = parseToLists(input)
            val countInRight = right.groupingBy { it }.eachCount()
            left.sumOf { n -> (countInRight[n] ?: 0) * n }
        }
        verify {
            expect result 24316233
            run test 1 expect 31
        }
    }
}

private fun parseToLists(input: Input): Pair<MutableList<Int>, MutableList<Int>> {
    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()
    input.lines
        .map { it.split("   ") }
        .forEach { (a, b) ->
            left.add(a.toInt())
            right.add(b.toInt())
        }
    return Pair(left, right)
}
