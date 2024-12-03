package day03

import common.day
import common.util.log

// answer #1: 183788984
// answer #2: 62098619

fun main() {
    day(n = 3) {
        part1 { input ->
            val regex = """(mul\([0-9]{1,3},[0-9]{1,3}\))""".toRegex()
            var sum = 0
            input.lines.map {
                regex.findAll(it).forEach { match ->
                    match.destructured.let { (string) ->
                        sum += string.drop(4).dropLast(1).split(",").map(String::toInt)
                            .reduce { a, b -> a * b }
                    }
                }
            }
            sum
        }
        verify {
            expect result 183788984
            run test 1 expect 161
        }

        part2 { input ->
            val regex = """(mul\([0-9]{1,3},[0-9]{1,3}\)|do\(\)|don't\(\))""".toRegex()
            var sum = 0
            var enabled = true
            input.lines.map {
                regex.findAll(it).forEach { match ->
                    match.destructured.let { (string) ->
                        string.log()
                        when (string) {
                            "do()" -> enabled = true
                            "don't()" -> enabled = false
                            else -> {
                                if (enabled) {
                                    sum += string.drop(4).dropLast(1).split(",").map(String::toInt)
                                        .reduce { a, b -> a * b }
                                }
                            }
                        }
                    }
                }
            }
            sum
        }
        verify {
            expect result 62098619
            run test 2 expect 48
        }
    }
}
