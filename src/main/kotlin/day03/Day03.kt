package day03

import common.day

// answer #1: 183788984
// answer #2: 62098619

fun main() {
    day(n = 3) {
        part1 { input ->
            val regex = """(mul\([0-9]{1,3},[0-9]{1,3}\))""".toRegex()
            input.lines.sumOf { line ->
                regex.findAll(line).sumOf { match ->
                    match.destructured.let { (instruction) ->
                        evaluate(instruction)
                    }
                }
            }
        }
        verify {
            expect result 183788984
            run test 1 expect 161
        }

        part2 { input ->
            val regex = """(mul\([0-9]{1,3},[0-9]{1,3}\)|do\(\)|don't\(\))""".toRegex()
            var enabled = true
            input.lines.sumOf { line ->
                regex.findAll(line).sumOf { match ->
                    match.destructured.let { (instruction) ->
                        when (instruction) {
                            "do()" -> enabled = true
                            "don't()" -> enabled = false
                            else if (enabled) -> return@let evaluate(instruction)
                        }
                        return@let 0
                    }
                }
            }
        }
        verify {
            expect result 62098619
            run test 2 expect 48
        }
    }
}

private fun evaluate(instruction: String) =
    instruction
        .dropWhile { !it.isDigit() }
        .dropLast(1)
        .split(",")
        .map(String::toInt)
        .reduce(Int::times)
