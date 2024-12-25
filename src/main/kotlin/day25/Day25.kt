package day25

import common.day
import common.util.sliceByBlank

// answer #1: 2933
// answer #2: N/A

fun main() {
    day(n = 25) {
        part1 { input ->
            val (keys, locks) = input.lines.sliceByBlank()
                .map { lines ->
                    val values = MutableList(5) { 0 }
                    lines.subList(1, 6).forEach { line ->
                        line.forEachIndexed { index, i ->
                            values[index] += if (i == '#') 1 else 0
                        }
                    }
                    lines.first().first() to values
                }
                .partition { it.first == '#' }
                .let { (a, b) -> a.map { it.second } to b.map { it.second } }

            fun isMatch(key: List<Int>, lock: List<Int>) =
                key.zip(lock).all { (a, b) -> a + b <= 5 }

            keys.sumOf { key -> locks.count { lock -> isMatch(key, lock) } }
        }
        verify {
            expect result 2933
            run test 1 expect 3
        }

        part2 {
            // N/A no part 2
        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

