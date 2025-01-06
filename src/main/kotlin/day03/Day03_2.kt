package day03

import common.day

// answer #1: 183788984
// answer #2: 62098619

fun main() {
    day(n = 3) {
        part1 { input ->
            val joined = input.lines.joinToString("")
            val regex = """mul\((\d+),(\d+)\)""".toRegex()
            regex.findAll(joined).sumOf(::evaluate)
        }
        verify {
            expect result 183788984
            run test 1 expect 161
        }

        part2 { input ->
            val joined = input.lines.joinToString("")
            val ignoredRegex = """don't\(\).*?(do\(\)|$)""".toRegex()
            val regex = """mul\((\d+),(\d+)\)""".toRegex()
            regex.findAll(joined.replace(ignoredRegex, "")).sumOf(::evaluate)
        }
        verify {
            expect result 62098619
            run test 2 expect 48
        }
    }
}

private fun evaluate(match: MatchResult) =
    match.destructured.let { (a, b) -> a.toInt() * b.toInt() }
