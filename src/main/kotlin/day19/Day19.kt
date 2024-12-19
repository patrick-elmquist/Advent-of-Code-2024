package day19

import common.Input
import common.day
import common.util.sliceByBlank

// answer #1: 317
// answer #2: 883443544805484

fun main() {
    day(n = 19) {
        part1 { input ->
            val (towels, arrangements) = parseInput(input)
            arrangements.count { countAllVersions(it, towels) > 0 }
        }
        verify {
            expect result 317
            run test 1 expect 6
        }

        part2 { input ->
            val (towels, arrangements) = parseInput(input)
            arrangements.sumOf { countAllVersions(it, towels) }
        }
        verify {
            expect result 883443544805484L
            run test 1 expect 16L
        }
    }
}

private fun countAllVersions(
    string: String,
    towels: Set<String>,
    seen: MutableMap<String, Long> = mutableMapOf(),
): Long = seen.getOrPut(string) {
    towels.filter { string.startsWith(it) }
        .map { string.removePrefix(it) }
        .sumOf { remaining ->
            if (remaining.isEmpty()) 1L else countAllVersions(remaining, towels, seen)
        }
}

private fun parseInput(input: Input): Pair<Set<String>, List<String>> {
    val (top, arrangements) = input.lines.sliceByBlank()
    val towels = top.first().split(", ").toSet()
    return towels to arrangements
}
