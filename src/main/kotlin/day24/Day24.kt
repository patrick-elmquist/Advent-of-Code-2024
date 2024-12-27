package day24

import common.Input
import common.day
import common.util.UnsafeMap
import common.util.asUnsafe
import common.util.log
import common.util.out
import common.util.pow
import common.util.printMermaidGraph
import common.util.sliceByBlank

// answer #1: 46362252142374
// answer #2: cbd,gmh,jmq,qrh,rqf,z06,z13,z38

fun main() {
    day(n = 24) {
        part1 { input ->
            val (initialValues, gates) = parseInitialValuesAndGates(input)

            val values = initialValues.toMutableMap().asUnsafe()
            val queue = ArrayDeque(gates.keys)
            while (queue.isNotEmpty()) {
                val entry = queue
                    .first { (a, b, _) -> a in values && b in values }
                    .also { queue.remove(it) }
                val (a, b, op) = entry
                val output = gates[entry]
                values[output] = op(values[a], values[b])
            }

            values.entries
                .filter { (key, _) -> key.startsWith('z') }
                .sortedBy { (key, _) -> key }
                .mapIndexed { index, (_, value) -> value * 2L.pow(index) }
                .sum()
        }
        verify {
            expect result 46362252142374L
            run test 1 expect 4L
        }

        part2 { input ->
            val (_, bottom) = input.lines.sliceByBlank()

            val gates = mutableMapOf<Triple<String, String, String>, String>()

            bottom.forEach {
                val (g, output) = it.split(" -> ")
                val (a, op, b) = g.split(" ")
                val key = Triple(a, b, op)
                gates[key] = output
            }

            // Approach:
            // Create and log a string for rendering a mermaid diagram
            // of the flow and manually track down weirdness in the adder.
            printMermaidGraph {
                gates
                    .forEach { (a, b, op), out ->
                        val opString = "${op}___${a}.$b"
                        addRow(a, opString)
                        addRow(b, opString)
                        addRow(opString, out)
                    }
            }
//            buildList {
//                gates.forEach { (a, b, op), out ->
//                    val opString = "${op}___${a}_$b"
//                    add("    $a --> $opString")
//                    add("    $b --> $opString")
//                    out.forEach {
//                        add("    $opString --> $it")
//                    }
//                }
//            }.sorted().joinToString("\n")
//                .log()

            // z06 and jmq should be swapped
            // gmh and z13 should be swapped
            // z38 and qrh should be swapped
            // rqf and cbd should be swapped
            listOf(
                "z06",
                "jmq",
                "gmh",
                "z13",
                "z38",
                "qrh",
                "rqf",
                "cbd",
            ).sorted().joinToString(",")
        }
        verify {
            expect result "cbd,gmh,jmq,qrh,rqf,z06,z13,z38"
        }
    }
}

private enum class Operation(val op: (a: Int, b: Int) -> Int) {
    AND({ a, b -> a and b }),
    OR({ a, b -> a or b }),
    XOR({ a, b -> a xor b });

    operator fun invoke(a: Int, b: Int) = op(a, b)
}

private fun parseInitialValuesAndGates(
    input: Input,
): Pair<Map<String, Int>, UnsafeMap<Triple<String, String, Operation>, String>> {
    val (top, bottom) = input.lines.sliceByBlank()

    val initialValues = top.associate {
        val (name, value) = it.split(": ")
        name to value.toInt()
    }

    val gates = bottom.associate {
        val (g, output) = it.split(" -> ")
        val (a, op, b) = g.split(" ")
        val key = Triple(a, b, Operation.valueOf(op))
        key to output
    }.asUnsafe()

    return Pair(initialValues, gates)
}
