package day02

import common.Input
import common.day
import kotlin.math.abs

// answer #1: 213
// answer #2: 285

fun main() {
    day(n = 2) {
        part1 { input ->
            parseReports(input).count { report -> report.areLevelsSafe() }
        }
        verify {
            expect result 213
            run test 1 expect 2
        }

        part2 { input ->
            parseReports(input)
                .count { report -> report.areLevelsSafe() || report.runBruteForceCheck() }
        }
        verify {
            expect result 285
            run test 1 expect 4
        }
    }
}

private fun parseReports(input: Input) = input.lines
    .map { line -> line.split(" ").map(String::toInt) }

private fun List<Int>.areLevelsSafe(): Boolean =
    checkIncreasingLevels() || checkDecreasingLevels()

private fun List<Int>.checkIncreasingLevels() =
    zipWithNext().all { (a, b) -> a < b && checkInterval(a, b) }

private fun List<Int>.checkDecreasingLevels() =
    zipWithNext().all { (a, b) -> a > b && checkInterval(a, b) }

private fun checkInterval(a: Int, b: Int) =
    abs(a - b) in (1..3)

private fun List<Int>.runBruteForceCheck() =
    indices.any { index ->
        toMutableList()
            .apply { removeAt(index) }
            .areLevelsSafe()
    }
