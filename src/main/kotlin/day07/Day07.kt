package day07

import common.Input
import common.day

// answer #1: 303766880536
// answer #2: 337041851384440

fun main() {
    day(n = 7) {
        part1 { input ->
            parseInputToExpectedAndValues(input)
                .filter { (result, values) ->
                    isValid(result, values, useCombine = false)
                }
                .sumOf { (expected, _) -> expected }
        }
        verify {
            expect result 303766880536L
            run test 1 expect 3749L
        }

        part2 { input ->
            parseInputToExpectedAndValues(input)
                .filter { (result, values) ->
                    isValid(result, values, useCombine = true)
                }
                .sumOf { (expected, _) -> expected }
        }
        verify {
            expect result 337041851384440L
            run test 1 expect 11387L
        }
    }
}

private fun isValid(expected: Long, values: List<Long>, useCombine: Boolean): Boolean =
    evaluate(expected, values.first(), values.drop(1), useCombine)

private fun evaluate(expected: Long, current: Long, values: List<Long>, useCombine: Boolean = false): Boolean {
    if (values.isEmpty()) return current == expected

    val value = values.first()
    val remaining = values.drop(1)

    if (evaluate(expected, current + value, remaining, useCombine)) return true
    if (evaluate(expected, current * value, remaining, useCombine)) return true
    return useCombine && evaluate(expected, "$current$value".toLong(), remaining, useCombine)
}

private fun parseInputToExpectedAndValues(input: Input): List<Pair<Long, List<Long>>> =
    input.lines.map { line ->
        line.split(": ").let { (result, values) ->
            result.toLong() to values.split(" ").map(String::toLong)
        }
    }
