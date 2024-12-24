package day24

import common.day
import common.util.log
import common.util.sliceByBlank
import kotlin.math.pow

// answer #1: 46362252142374
// answer #2: not cbc,gmh,jmq,qrh,rqf,z06,z13,z38

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
                gates.getOrPut(key) { mutableSetOf<String>() }.add(output)
            }

            val values = mutableMapOf<String, Boolean>()
            values.putAll(initial)

            val queue = ArrayDeque<Triple<String, String, String>>(gates.keys)

            while (queue.isNotEmpty()) {
                val entry = queue.first { it.first in values && it.second in values }
                queue.remove(entry)
                val (a, b, op) = entry
                val destinations = gates.getValue(entry)

                val aValue = values.getValue(a)
                val bValue = values.getValue(b)
                val value = when (op) {
                    "AND" -> aValue and bValue
                    "OR" -> aValue or bValue
                    "XOR" -> aValue xor bValue
                    else -> error("$op")
                }
                destinations.forEach { values[it] = value }
            }

            values.entries
                .filter { it.key.startsWith('z') }
                .sortedBy { it.key }
                .mapIndexed { i, (key, value) ->
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
            val (top, bottom) = input.lines.sliceByBlank()

            val initial = top.map {
                val (name, value) = it.split(": ")
                name to (value == "1")
            }.toMutableList()

            val gates = mutableMapOf<Triple<String, String, String>, MutableSet<String>>()
            

            
            
            val gatesReversed = mutableMapOf<String, Triple<String, String, String>>()
            bottom.forEach {
                val (g, output) = it.split(" -> ")
                val (a, op, b) = g.split(" ")
                val key = Triple(a, b, op)
                gates.getOrPut(key) { mutableSetOf<String>() }.add(output)
                gatesReversed.getOrPut(output) { key }
            }

            buildList<String> {
                gates.forEach { (a, b, op), out ->
                    val opString = "${op}___${a}_$b"
                    add("    $a --> $opString")
                    add("    $b --> $opString")
                    out.forEach {
                        add("    $opString --> $it")
                    }
                }
            }.sorted().joinToString("\n").log()

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
                ).sorted().joinToString(",").log()
            TODO()
            val values = calculateValues(initial, gates)
            fun toDecimal(list: List<Pair<String, Boolean>>): Long {
                return list.sortedBy { it.first }
                    .mapIndexed { i, (key, value) ->
                        if (value) {
                            2f.pow(i).toLong()
                        } else {
                            0L
                        }
                    }
                    .sum()
            }

            val (x, y) = initial.partition { it.first.startsWith('x') }
            val xDec = toDecimal(x).log("x:")
            val yDec = toDecimal(y).log("y:")
            val zDec = if (initial.size < 15) {
                (xDec and yDec)
            } else {
                (xDec + yDec)
            }.log("z:")
            x.log("x:")
            y.log("y:")
            val zBinary = decimalToBinary(zDec).log("fun:")
                .mapIndexed { index, i -> "z${index.toString().padStart(2, '0')}" to (i == "1") }
                .log()

            initial.addAll(zBinary)
            initial.log()
            values.entries.filter { it.key.startsWith('z') }.sortedBy { it.key }
                .mapIndexed { i, it -> it.value != zBinary[i].log("${it.key}==").second }
                .log()

            zBinary.forEach { values[it.first] = it.second }

            fun rec(
                node: String,
                expect: Boolean,
                broken: MutableList<Triple<String, String, String>>,
                values: Map<String, Boolean>,
            ): Boolean {
                "node:$node $expect (${values.getValue(node)})".log()

                if (node.startsWith("x") || node.startsWith("y")) {
                    return values[node] == expect
                }

                val triple = gatesReversed.getValue(node)
                triple.log("triple:")
                val (a, b, op) = triple
                val valueA = values.getValue(a)
                val valueB = values.getValue(b)
                val isValid = when (op) {
                    "AND" -> valueA and valueB
                    "OR" -> valueA or valueB
                    "XOR" -> valueA xor valueB
                    else -> error("$op")
                } == expect

                if (isValid) {
                    // TODO we are detecting the error in the wrong end now
                    // need to go top down to see where the error is

                } else {
                    broken.add(triple)
                }
                return true
            }

            var last = listOf<String>()
            for (i in 0..44) {
                val list = mutableListOf<String>()
                val name = "z" + i.toString().padStart(2, '0')
                backtrackOp(name, list, gatesReversed)
                val size = list.size
                val diff = size - last.size
                if (diff != 8) {
                    "$i ($size, not 8, $diff) ".log()
                    list.log()
                    val borked = mutableListOf<Triple<String, String, String>>()
                    rec(name, expect = zBinary[i].second,values = values, broken = borked)
                    val max = list.maxOf { it.length }
                    list.filter { it.length == max }.log("only:")
                    if (borked.isNotEmpty()) {
                        borked.log("borked:")
                        gates.getValue(borked.first()).log("borked output:")
                    }
                } else {
                    "z$i ($size)".log()
                }
                last = list
            }
            TODO()
            val broken = mutableListOf<Triple<String, String, String>>()
            zBinary.forEach { (name, value) ->
                rec(name, value, broken, values)
            }
            val allBroken = mutableSetOf<String>()
            while (broken.isNotEmpty()) {
                broken.log()
                require(broken.size == 2)
                val (a, b) = broken
                val (akey, avalue) = gatesReversed.entries.first { it.value == a }
                val (bkey, bvalue) = gatesReversed.entries.first { it.value == b }

                gatesReversed[akey] = bvalue
                gatesReversed[bkey] = avalue
                allBroken.add(akey)
                allBroken.add(bkey)
                gatesReversed.log()
                broken.clear()
                zBinary.forEach { (name, value) ->
                    rec(name, value, broken, values)
                }
            }

            allBroken.log()
        }
        verify {
//            breakAfterTest()
            expect result null
//            run test 3 expect Unit
        }
    }
}

private fun calculateValues(
    initial: MutableList<Pair<String, Boolean>>,
    gates: MutableMap<Triple<String, String, String>, MutableSet<String>>,
): MutableMap<String, Boolean> {
    val values = mutableMapOf<String, Boolean>()
    values.putAll(initial)

    val queue = ArrayDeque<Triple<String, String, String>>(gates.keys)

    while (queue.isNotEmpty()) {
        val entry = queue.first { it.first in values && it.second in values }
        queue.remove(entry)
        val (a, b, op) = entry
        val destinations = gates.getValue(entry)

        val aValue = values.getValue(a)
        val bValue = values.getValue(b)
        val value = when (op) {
            "AND" -> aValue and bValue
            "OR" -> aValue or bValue
            "XOR" -> aValue xor bValue
            else -> error("$op")
        }
        destinations.forEach { values[it] = value }
    }
    return values
}

private fun decimalToBinary(n: Long): List<String> {
    val intList = mutableListOf<Long>()
    var decimalNumber = n
    var result = ""
    var i = 0L

    while (decimalNumber > 0L) {
        intList.add(decimalNumber % 2L)
        decimalNumber /= 2L
    }
    return intList.map(Long::toString)
}


private fun backtrackOp(
    node: String,
    list: MutableList<String>,
    reversed: Map<String, Triple<String, String, String>>,
) {
    val queue = ArrayDeque<Pair<String, List<String>>>()
    queue.add(node to emptyList())

    while (queue.isNotEmpty()) {
        val q = queue.removeFirst()
        list += (q.second + q.first).joinToString(",")
        if (q.first in reversed) {
            val t = reversed.getValue(q.first)
            queue += t.first to (q.second + q.first)
            queue += t.second to (q.second + q.first)
        }
    }
}

private fun backtrackOp2(
    node: String,
    list: MutableList<String>,
    reversed: Map<String, Triple<String, String, String>>,
) {
    val queue = ArrayDeque<Pair<String, List<String>>>()
    queue.add(node to emptyList())

    while (queue.isNotEmpty()) {
        val q = queue.removeFirst()
        list += (q.second + q.first).joinToString(",")
        if (q.first in reversed) {
            val t = reversed.getValue(q.first)
            queue += t.first to (q.second + q.first)
            queue += t.second to (q.second + q.first)
        }
    }
}
