package day02

import common.Input
import common.day
import kotlin.math.abs

// answer #1:
// answer #2: not 307, not 259

fun main() {
    day(n = 2) {
        part1 { input ->
            parseReports(input).count { report -> report.checkLevels() }
        }
        verify {
            expect result 213
            run test 1 expect 2
        }

        part2 { input ->
            parseReports(input)
                .count { report -> report.checkLevels() || report.tryBruteForce() }
        }
        verify {
            expect result 285
            run test 1 expect 4
        }
    }
}

private fun parseReports(input: Input) = input.lines
    .map { line -> line.split(" ").map(String::toInt) }

private fun List<Int>.checkLevels(): Boolean =
    checkLevels { a, b -> a < b } || checkLevels { a, b -> a > b }

private fun List<Int>.checkLevels(condition: (a: Int, b: Int) -> Boolean) =
    zipWithNext().all { (a, b) -> condition(a, b) && abs(a - b) in (1..3) }

private fun List<Int>.tryBruteForce() =
    indices.any { index ->
        toMutableList()
            .apply { removeAt(index) }
            .checkLevels()
    }

