package day05

import common.day
import common.util.log
import common.util.sliceByBlank

// answer #1: 5208
// answer #2: 6732

fun main() {
    day(n = 5) {
        part1 { input ->
            val (top, bottom) = input.lines.sliceByBlank()

            val needToBeBeforeMe = top.map { string -> string.split("|").map(String::toInt) }
                .map { (a, b) -> a to b }
                .groupBy { pair -> pair.second }
                .mapValues { entry -> entry.value.map { pair -> pair.first } }

            val lines = bottom.map { string -> string.split(",").map(String::toInt) }

            lines.filter { line ->
                line.forEachIndexed { index, number ->
                    val numbersShouldNotBeAfter = needToBeBeforeMe[number]?.toSet() ?: emptySet()
                    val end = line.subList(index + 1, line.size)
                    val validEnd = end.all { n -> n !in numbersShouldNotBeAfter }
                    if (!validEnd) {
                        return@filter false
                    }
                }
                true
            }.sumOf { ints -> ints[ints.size / 2] }
        }
        verify {
            expect result 5208
            run test 1 expect 143
        }

        part2 { input ->
            val (top, bottom) = input.lines.sliceByBlank()

            val needToBeBeforeMe = top.map { string -> string.split("|").map(String::toInt) }
                .map { (a, b) -> a to b }
                .groupBy { pair -> pair.second }
                .mapValues { entry -> entry.value.map { pair -> pair.first } }

            val lines = bottom.map { string -> string.split(",").map(String::toInt) }

            val invalidLines = lines.filter { line ->
                line.forEachIndexed { index, number ->
                    val numbersShouldNotBeAfter = needToBeBeforeMe[number]?.toSet() ?: emptySet()
                    val end = line.subList(index + 1, line.size)
                    val validEnd = end.all { n -> n !in numbersShouldNotBeAfter }
                    if (!validEnd) {
                        return@filter true
                    }
                }
                false
            }

            "invalid lines:".log()
            invalidLines.joinToString("\n").log()

            val sorted = invalidLines.map { line ->
                line.sortedWith { o1, o2 ->
                    val pre1 = needToBeBeforeMe[o1]?.toSet() ?: emptySet()
                    val pre2 = needToBeBeforeMe[o2]?.toSet() ?: emptySet()

                    if (o2 in pre1) {
                        -1
                    } else if (o1 in pre2) {
                        1
                    } else {
                        0
                    }
                }
            }

            "sorted lists:".log()
            sorted.joinToString("\n").log()

            sorted.sumOf { ints -> ints[ints.size / 2] }


        }
        verify {
            expect result 6732
            run test 1 expect 123
        }
    }
}
