package day24

import common.day
import common.util.log
import common.util.sliceByBlank
import common.util.unsafeMapOf
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
            val (top, bottom) = input.lines.sliceByBlank()

            val initial = top.map {
                val (name, value) = it.split(": ")
                name to (value == "1")
            }

            val gates = mutableMapOf<Triple<String, String, String>, MutableSet<String>>()
            bottom.forEach {
                val (g, output) = it.split(" -> ")
                val (a, op, b) = g.split(" ")
                val key = Triple(a, b, op)
                gates.getOrPut(key) { mutableSetOf() }.add(output)
            }

            val values = unsafeMapOf<String, Boolean>()
            values.putAll(initial)

            val queue = ArrayDeque(gates.keys)

            while (queue.isNotEmpty()) {
                val entry = queue.first { it.first in values && it.second in values }
                queue.remove(entry)
                val (a, b, op) = entry
                val destinations = gates.getValue(entry)

                val operation = Operation.valueOf(op)
                val value = operation(values[a], values[b])
                destinations.forEach { values[it] = value }
            }

            values.entries
                .filter { it.key.startsWith('z') }
                .sortedBy { it.key }
                .mapIndexed { i, (_, value) ->
                    if (value) {
                        2f.pow(i).toLong()
                    } else {
                        0L
                    }
                }
                .sum()
        }
        verify {
            expect result 46362252142374L
            run test 1 expect 4L
            run test 2 expect 2024L
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
