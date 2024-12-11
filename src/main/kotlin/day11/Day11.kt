package day11

import common.day

// answer #1: 198075
// answer #2: 235571309320764

fun main() {
    day(n = 11) {
        part1 { input ->
            val stones = input.lines.first().split(" ").map(String::toLong)
            stones.sumOf { number -> rec(blinksRemaining = 25, number = number) }
        }
        verify {
            expect result 198075L
        }

        part2 { input ->
            val stones = input.lines.first().split(" ").map(String::toLong)
            stones.sumOf { number -> rec(blinksRemaining = 75, number = number) }
        }
        verify {
            expect result 235571309320764L
        }
    }
}

private fun rec(
    blinksRemaining: Int,
    number: Long,
    state: MutableMap<Pair<Long, Int>, Long> = mutableMapOf(),
): Long {
    if (blinksRemaining == 0) return 1
    val cache = state[number to blinksRemaining]
    if (cache != null) return cache

    val list = buildList {
        if (number == 0L) {
            add(1L)
            return@buildList
        }

        val string = number.toString()
        if (string.length % 2 == 0) {
            val a = string.substring(0, string.length / 2)
            val b = string.substring(string.length / 2)
            add(a.toLong())
            add(b.toLong())
            return@buildList
        }

        add(number * 2024L)
    }

    val result = list.sumOf {
        rec(
            blinksRemaining = blinksRemaining - 1,
            number = it,
            state = state,
        )
    }
    state[number to blinksRemaining] = result
    return result
}
