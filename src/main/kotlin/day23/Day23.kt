package day23

import common.day
import kotlin.collections.orEmpty

// answer #1: 1173
// answer #2:

private val interResult = listOf(
    listOf("aq,cg,yn"),
    listOf("aq,vc,wq"),
    listOf("co,de,ka"),
    listOf("co,de,ta"),
    listOf("co,ka,ta"),
    listOf("de,ka,ta"),
    listOf("kh,qp,ub"),
    listOf("qp,td,wh"),
    listOf("tb,vc,wq"),
    listOf("tc,td,wh"),
    listOf("td,wh,yn"),
    listOf("ub,vc,wq"),
).map { it.map { it.split(',') } }

fun main() {
    day(n = 23) {
        part1 { input ->
            val allPairs = input.lines.flatMap {
                it.split('-').sorted().let { (a,b) -> listOf(a to b, b to a)}
            }.distinct()

            val connections = allPairs.groupBy { it.first }
                .mapValues { (_, value) -> value.map { it.second }.toSet() }


            val count = connections.keys.map { node ->
                connections.countCyclesOfThree(node)
            }

            val counted = count.flatMap { it }.toSet().count { it.any { it.startsWith('t') } }
            counted
        }
        verify {
            expect result 1173
            run test 1 expect 7
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun Map<String, Set<String>>.countCyclesOfThree(node: String): Set<List<String>> {
    val others = getValue(node)
    var cycles = mutableSetOf<List<String>>()
    for (a in others) {
        val aOthers = getValue(a)
        for (b in others) {
            if (a == b) continue
            if (b in aOthers) {
                cycles.add(listOf(node, a, b).sorted())
            }
        }
    }
    return cycles
}

private fun isCyclicUtil(
    node: String,
    connections: Map<String, Set<String>>,
    visited: MutableSet<String>,
    parent: String,
): Boolean {
    visited += node

    for (a in connections[node].orEmpty()) {
        if (a !in visited) {
            if (isCyclicUtil(a, connections, visited, node)) {
                return true
            }
        } else if (a != parent) {
            return true
        }
    }
    return false
}

private fun dfs(
    connections: Map<String, Set<String>>,
    visited: MutableSet<String>,
    n: Int,
    vert: String,
    start: String,
): Int {
    visited += vert

    val connectedTo = connections[vert].orEmpty()

    if (n == 0) {
        visited -= vert
        return if (start in connectedTo) 1 else 0
    }

    var sum = 0
    for (connection in connectedTo) {
        if (connection !in visited) {
            sum += dfs(connections, visited, n - 1, connection, start)
        }
    }

    visited -= vert

    return sum
}

private fun countCycles(connections: Map<String, Set<String>>, n: Int = 3): Int {
    val visited = mutableSetOf<String>()
    val keys = connections.keys

    var sum = 0
    for (key in keys) {
        sum += dfs(connections, visited, n - 1, key, key)
    }

    return sum
}

private fun isCyclic(node: String, connections: Map<String, Set<String>>): Boolean {
    val visited = mutableSetOf<String>()
    visited += node

    val connectedTo = connections[node].orEmpty()
    for (connectedNode in connectedTo) {
        if (connectedNode !in visited) {
            if (isCyclicUtil(connectedNode, connections, visited, node)) {
                return true
            }
        }
    }
    return false
}

