package day05

import common.Input
import common.day
import common.util.sliceByBlank
import java.util.ArrayDeque

// answer #1: 5208
// answer #2: 6732

fun main() {
    day(n = 5) {
        part1 { input ->
            val (rules, updates) = parseInput(input)
            updates
                .filter { update -> update.isValid(rules) }
                .sumOf { numbers -> numbers[numbers.size / 2] }
        }
        verify {
            expect result 5208
            run test 1 expect 143
        }

        part2 { input ->
            val (rules, updates) = parseInput(input)
            updates
                .filterNot { update -> update.isValid(rules) }
                .map { line ->
                    line.sortedWith { o1, o2 ->
                        when {
                            o2 in rules[o1].orEmpty() -> -1
                            o1 in rules[o2].orEmpty() -> 1
                            else -> 0
                        }
                    }
                }
                .sumOf { numbers -> numbers[numbers.size / 2] }
        }
        verify {
            expect result 6732
            run test 1 expect 123
        }
    }
}

private fun Collection<Int>.isValid(rulesMap: Map<Int, Set<Int>>): Boolean {
    val deque = ArrayDeque(this)
    while (deque.isNotEmpty()) {
        val number = deque.removeFirst()
        val notAllowed = rulesMap[number].orEmpty()
        if (deque.any { n -> n in notAllowed }) {
            return false
        }
    }
    return true
}

private fun parseInput(input: Input): Pair<Map<Int, Set<Int>>, List<List<Int>>> =
    input.lines.sliceByBlank()
        .let { (top, bottom) -> createRequirementsMap(top) to createUpdatesList(bottom) }

private fun createUpdatesList(bottom: List<String>): List<List<Int>> =
    bottom.map { it.split(",").map(String::toInt) }

private fun createRequirementsMap(top: List<String>): Map<Int, Set<Int>> =
    top.map { it.split("|").map(String::toInt) }
        .map { (a, b) -> a to b }
        .groupBy { (_, b) -> b }
        .mapValues { (_, value) -> value.map { it.first }.toSet() }
