package day23

import common.Input
import common.day
import kotlin.collections.component1
import kotlin.collections.component2

// answer #1: 1173
// answer #2: cm,de,ez,gv,hg,iy,or,pw,qu,rs,sn,uc,wq

fun main() {
    day(n = 23) {
        part1 { input ->
            val connections = buildConnectionsMap(input)
            connections.keys
                .flatMap { node -> connections.countCyclesOfThree(node) }
                .toSet()
                .count { cycle -> cycle.any { it.startsWith('t') } }
        }
        verify {
            expect result 1173
            run test 1 expect 7
        }

        part2 { input ->
            // TODO revisit with proper Bron–Kerbosch
            val connections = buildConnectionsMap(input)
            connections
                .findCycles()
                .filter { cycle ->
                    val cycleNeighbors = cycle
                        .map { node -> connections.getValue(node) + node }
                    cycle.all { c -> cycleNeighbors.all { c in it } }
                }
                .maxBy { it.size }
                .sorted()
                .joinToString(",")
        }
        verify {
            expect result "cm,de,ez,gv,hg,iy,or,pw,qu,rs,sn,uc,wq"
            run test 1 expect "co,de,ka,ta"
        }
    }
}

private fun Map<String, Set<String>>.findCycles(): List<List<String>> {
    val (parent, neighbors) = entries.toList().first()
    val node = neighbors.first()
    return buildList { dfsCycle(node = node, parent = parent, cycles = this) }
}

private fun Map<String, Set<String>>.dfsCycle(
    node: String,
    parent: String,
    cycles: MutableList<List<String>>,
    color: MutableMap<String, Int> = mutableMapOf(),
    parents: MutableMap<String, String> = mutableMapOf(),
) {
    when (color[node]) {
        1 -> {
            val cycle = buildList {
                add(parent)
                var current = parent
                while (last() != node) {
                    current = parents.getValue(current)
                    add(current)
                }
            }
            cycles.add(cycle)
        }

        2 -> Unit

        else -> {
            parents[node] = parent
            color[node] = 1

            val neighbors = getValue(node)
            for (neighbor in neighbors) {
                if (neighbor == parents[node]) continue
                dfsCycle(neighbor, node, cycles, color, parents)
            }

            color[node] = 2
        }
    }
}

private fun Map<String, Set<String>>.countCyclesOfThree(node: String) =
    buildSet {
        val others = getValue(node)
        for (a in others) {
            val aNeighbors = getValue(a)
            for (b in others) {
                if (a == b) continue
                if (b in aNeighbors) add(listOf(node, a, b).sorted())
            }
        }
    }

private fun buildConnectionsMap(input: Input): Map<String, Set<String>> =
    buildMap<String, MutableSet<String>> {
        input.lines.forEach {
            it.split('-').let { (a, b) ->
                getOrPut(a) { mutableSetOf() }.add(b)
                getOrPut(b) { mutableSetOf() }.add(a)
            }
        }
    }
