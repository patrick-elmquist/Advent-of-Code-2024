package day17

import common.day
import common.util.sliceByBlank
import kotlin.math.pow

// answer #1: 2,7,6,5,6,0,2,3,1
// answer #2:

fun main() {
    day(n = 17) {
        part1 { input ->
            val sliced = input.lines.sliceByBlank()
            var registers = sliced.first().map { it.split(" ").last().toInt() }
            println(registers)

            val instructions = sliced.last().first()
                .removePrefix("Program: ")
                .split(",")
                .map(String::toInt)

            println(instructions)
            runProgram(registers, instructions)
        }
        verify {
            expect result "2,7,6,5,6,0,2,3,1"
            run test 1 expect "4,6,3,5,6,3,5,2,1,0"
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun runProgram(registers: List<Int>, instructions: List<Int>): String {
    var (a, b, c) = registers
    fun getComboOperand(value: Int): Int {
        return when (value) {
            in 0..3 -> value
            4 -> a
            5 -> b
            6 -> c
            else -> error("$value not valid")
        }
    }

    val output = mutableListOf<Int>()
    var pointer = 0
    while (true) {
        if (pointer !in instructions.indices) break

        val instr = instructions[pointer]
        val literalOperand = instructions[pointer + 1]
        val comboOperand = getComboOperand(literalOperand)

        when (instr) {
            0 -> { // adv division
                a = (a / 2f.pow(comboOperand)).toInt()
                pointer += 2
            }

            1 -> { // bxl bitwise XOR
                b = b xor literalOperand
                pointer += 2
            }

            2 -> {
                b = comboOperand % 8
                pointer += 2
            }

            3 -> {
                if (a != 0) {
                    pointer = literalOperand
                } else {
                    pointer += 2
                }
            }

            4 -> {
                b = b xor c
                pointer += 2
            }

            5 -> {
                output.add(comboOperand % 8)
                pointer += 2
            }

            6 -> {
                b = (a / 2f.pow(comboOperand)).toInt()
                pointer += 2
            }

            7 -> {
                c = (a / 2f.pow(comboOperand)).toInt()
                pointer += 2
            }
        }
    }

    return output.joinToString(",")
}
