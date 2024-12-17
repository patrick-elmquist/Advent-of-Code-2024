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
            solve2(instructions, registers).min()
        }
        verify {
            expect result 107416870455451L
            run test 2 expect 117440L
        }
    }
}

private fun solve2(instructions: List<Long>, registers: List<Long>): List<Long> {
    val valid = mutableListOf(0L)
    for (length in 1..instructions.size) {
        val instr = instructions.takeLast(length)
        val oldValid = valid.toList()
        valid.clear()
        for (num in oldValid) {
            for (offset in 0L..7L) {
                val newNum = 8L * num + offset
                val output = runProgram(
                    registers = registers.toMutableList().apply { this[0] = newNum },
                    instructions,
                )
                if (output == instr) {
                    valid += newNum
                }
            }
        }
    }
    return valid
}

private fun runProgram(registers: List<Long>, instructions: List<Long>): List<Long> {
    var (a, b, c) = registers
    fun getComboOperand(value: Long): Long {
        return when (value) {
            in 0..3 -> value
            4L -> a
            5L -> b
            6L -> c
            else -> error("$value not valid")
        }
    }

    val output = mutableListOf<Long>()
    var pointer = 0
    while (true) {
        if (pointer !in instructions.indices) break

        val instr = instructions[pointer]
        val literalOperand = instructions[pointer + 1]
        val comboOperand = getComboOperand(literalOperand)

        when (instr) {
            0L -> a /= 2f.pow(comboOperand.toInt()).toLong()
            1L -> b = b xor literalOperand
            2L -> b = comboOperand % 8
            3L -> {
                if (a != 0L) {
                    pointer = literalOperand.toInt()
                    continue
                }
            }
            4L -> b = b xor c
            5L -> output.add(comboOperand % 8L)
            6L -> b = a / 2f.pow(comboOperand.toInt()).toLong()
            7L -> c = a / 2f.pow(comboOperand.toInt()).toLong()
        }
        pointer += 2
    }
    return output
}

private fun parseInput(input: Input): Pair<List<Long>, List<Long>> {
    val sliced = input.lines.sliceByBlank()
    val registers = sliced.first().map { it.split(" ").last().toLong() }
    val instructions = sliced.last().first()
        .removePrefix("Program: ")
        .split(",")
        .map(String::toLong)
    return Pair(registers, instructions)
}
