package day17

import common.Input
import common.day
import common.util.sliceByBlank
import kotlin.math.pow

// answer #1: 2,7,6,5,6,0,2,3,1
// answer #2: 107416870455451

fun main() {
    day(n = 17) {
        part1 { input ->
            val (registers, instructions) = parseInput(input)
            runProgram(registers, instructions).joinToString(",")
        }
        verify {
            expect result "2,7,6,5,6,0,2,3,1"
            run test 1 expect "4,6,3,5,6,3,5,2,1,0"
        }

        part2 { input ->
            val (registers, instructions) = parseInput(input)
            findPossibleAs(instructions, registers).min()
        }
        verify {
            expect result 107416870455451L
            run test 2 expect 117440L
        }
    }
}

private fun findPossibleAs(instructions: List<Long>, registers: List<Long>): List<Long> {
    val valid = mutableListOf(0L)
    for (length in 1..instructions.size) {
        val program = instructions.takeLast(length)
        val alternatives = valid.toList()
        valid.clear()
        for (num in alternatives) {
            for (offset in 0L..7L) {
                val newCandidate = 8L * num + offset
                val output = runProgram(
                    registers = registers.toMutableList()
                        .apply { this[0] = newCandidate },
                    instructions = instructions,
                )
                if (output == program) valid += newCandidate
            }
        }
    }
    return valid
}

private fun runProgram(registers: List<Long>, instructions: List<Long>): List<Long> {
    var (a, b, c) = registers
    fun comboOperand(value: Long): Long =
        when (value) {
            in 0..3 -> value
            4L -> a
            5L -> b
            6L -> c
            else -> error("$value not valid")
        }

    val output = mutableListOf<Long>()
    var pointer = 0
    while (true) {
        if (pointer !in instructions.indices) break
        val literalOperand = instructions[pointer + 1]
        val comboOperand = comboOperand(literalOperand)
        when (instructions[pointer]) {
            0L -> a /= 2L.pow(comboOperand)
            1L -> b = b xor literalOperand
            2L -> b = comboOperand % 8L
            3L -> {
                if (a != 0L) {
                    pointer = literalOperand.toInt()
                    continue
                }
            }

            4L -> b = b xor c
            5L -> output.add(comboOperand % 8L)
            6L -> b = a / 2L.pow(comboOperand)
            7L -> c = a / 2L.pow(comboOperand)
        }
        pointer += 2
    }
    return output
}

private fun Long.pow(n: Long): Long =
    this.toFloat().pow(n.toInt()).toLong()

private fun parseInput(input: Input): Pair<List<Long>, List<Long>> {
    val sliced = input.lines.sliceByBlank()
    val registers = sliced.first().map { it.split(" ").last().toLong() }
    val instructions = sliced.last().first()
        .removePrefix("Program: ")
        .split(",")
        .map(String::toLong)
    return Pair(registers, instructions)
}
