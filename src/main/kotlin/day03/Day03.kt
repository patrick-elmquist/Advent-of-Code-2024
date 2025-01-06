package day03

import common.day

// answer #1: 183788984
// answer #2: 62098619

fun main() {
    day(n = 3) {
        part1 { input ->
            val regex = """mul\((\d+),(\d+)\)""".toRegex()
            input.lines.sumOf { line -> regex.findAll(line).sumOf(::evaluate) }
        }
        verify {
            expect result 183788984
            run test 1 expect 161
        }

        part2 { input ->
            val regex = """mul\((\d+),(\d+)\)|do\(\)|don't\(\)""".toRegex()
            var enabled = true
            input.lines.sumOf { line ->
                regex.findAll(line).sumOf { match ->
                    when (match.value) {
                        "do()" -> enabled = true
                        "don't()" -> enabled = false
                        else if (enabled) -> return@sumOf evaluate(match)
                    }
                    return@sumOf 0
                }
            }
        }
        verify {
            expect result 62098619
            run test 2 expect 48
        }
    }
}

private fun evaluate(match: MatchResult) =
    match.destructured.let { (a, b) -> a.toInt() * b.toInt() }
