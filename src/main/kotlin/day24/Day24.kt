package day24

import common.Input
import common.day
import common.util.UnsafeMap
import common.util.asUnsafe
import common.util.pow
import common.util.sliceByBlank
import kotlin.math.pow

// answer #1: 46362252142374
// answer #2: cbd,gmh,jmq,qrh,rqf,z06,z13,z38

private enum class Operation(val op: (a: Boolean, b: Boolean) -> Boolean) {
    AND({ a, b -> a and b }),
    OR({ a, b -> a or b }),
    XOR({ a, b -> a xor b });

    operator fun invoke(a: Boolean, b: Boolean) = op(a, b)
}

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
                .filter { it.key.startsWith('z') }
                .sortedBy { it.key }
                .mapIndexed { i, (_, value) -> if (value) 2L.pow(i) else 0L }
                .sum()
        }
        verify {
            expect result 46362252142374L
            run test 1 expect 4L
        }

        part2 { input ->
            val (_, bottom) = input.lines.sliceByBlank()

            val gates = mutableMapOf<Triple<String, String, String>, MutableSet<String>>()

            bottom.forEach {
                val (g, output) = it.split(" -> ")
                val (a, op, b) = g.split(" ")
                val key = Triple(a, b, op)
                gates.getOrPut(key) { mutableSetOf() }.add(output)
            }

            // Approach:
            // Create and log a string for rendering a mermaid diagram
            // of the flow and manually track down weirdness in the adder.
            buildList {
                gates.forEach { (a, b, op), out ->
                    val opString = "${op}___${a}_$b"
                    add("    $a --> $opString")
                    add("    $b --> $opString")
                    out.forEach {
                        add("    $opString --> $it")
                    }
                }
            }.sorted().joinToString("\n")
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

private fun parseInitialValuesAndGates(input: Input): Pair<UnsafeMap<String, Boolean>, UnsafeMap<Triple<String, String, Operation>, String>> {
    val (top, bottom) = input.lines.sliceByBlank()

    val initialValues = top.associate {
        val (name, value) = it.split(": ")
        name to (value == "1")
    }.asUnsafe()

    val gates = bottom.associate {
        val (g, output) = it.split(" -> ")
        val (a, op, b) = g.split(" ")
        val key = Triple(a, b, Operation.valueOf(op))
        key to output
    }.asUnsafe()

    return Pair(initialValues, gates)
}
