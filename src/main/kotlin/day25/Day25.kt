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
                    lines.drop(1).dropLast(1).forEach {
                        it.forEachIndexed { index, i ->
                            values[index] += if (i == '#') 1 else 0
                        }
                    }

                    val isLock = lines.first().any { it == '#' }
                    if (isLock) 'L' to values else 'K' to values
                }.partition { it.first == 'K' }

            var match = 0
            for (key in keys.map { it.second }) {
                for (lock in locks.map { it.second }) {
                    val isMatch = key.zip(lock) { a, b -> a + b }.all { it <= 5 }
                    if (isMatch) match++
                }
            }
            match
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
