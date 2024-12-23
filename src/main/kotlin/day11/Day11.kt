package day11

import common.day
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

// answer #1: 198075
// answer #2: 235571309320764

fun main() {
    day(n = 11) {
        part1 { input ->
            val stones = input.lines.first().split(" ").map(String::toLong)
            stones.sumOf { number -> countStones(blinksRemaining = 25, number = number) }
        }
        verify {
            expect result 198075L
        }

        part2 { input ->
            val stones = input.lines.first().split(" ").map(String::toLong)
            stones.sumOf { number -> countStones(blinksRemaining = 75, number = number) }
        }
        verify {
            expect result 235571309320764L
        }
    }
}

private val Long.length: Int
    get() = when (this) {
        0L -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }

private val state: MutableMap<Pair<Long, Int>, Long> = mutableMapOf()
private fun countStones(
    blinksRemaining: Int,
    number: Long,
): Long {
    return state.getOrPut(number to blinksRemaining) {
        if (blinksRemaining == 0) {
            1
        } else {
            val len = number.length
            val stones = when {
                number == 0L -> listOf(1L)
                len % 2 == 0 -> {
                    listOf(
                        number / 10.0.pow(len / 2).toLong(),
                        number % 10.0.pow(len / 2).toLong(),
                    )
                }
                else -> listOf(number * 2024)
            }

            stones.sumOf { stone ->
                countStones(
                    blinksRemaining = blinksRemaining - 1,
                    number = stone,
                )
            }
        }
    }
}
