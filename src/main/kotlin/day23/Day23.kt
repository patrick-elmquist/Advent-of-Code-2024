package day23

import common.day

// answer #1: 1173
// answer #2: cm,de,ez,gv,hg,iy,or,pw,qu,rs,sn,uc,wq

fun main() {
    day(n = 23) {
        part1 { input ->
            val allPairs = input.lines.flatMap {
                it.split('-').sorted().let { (a, b) -> listOf(a to b, b to a) }
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
            val allPairs = input.lines.flatMap {
                it.split('-').sorted().let { (a, b) -> listOf(a to b, b to a) }
            }.distinct()

            val connections = allPairs.groupBy { it.first }
                .mapValues { (_, value) -> value.map { it.second }.toSet() }

            val node0 = connections.entries.toList().first()
            var cycles = mutableListOf<List<String>>()
            connections.dfsCycle(
                node = node0.value.first(),
                parent = node0.key,
                cycles = cycles,
            )
            val sortedCycles = cycles.map { it.sorted() }.sortedBy { it.size }

            sortedCycles.filter { cycle ->
                val s = cycle.map { connections.getValue(it) + it }
                    .reduce { a, b -> a.intersect(b) }
                cycle.all { it in s }
            }.maxBy { it.size }.sorted().joinToString(",")
        }
        verify {
            expect result "cm,de,ez,gv,hg,iy,or,pw,qu,rs,sn,uc,wq"
            run test 1 expect "co,de,ka,ta"
        }
    }
}

private fun Map<String, Set<String>>.dfsCycle(
    node: String,
    parent: String,
    colors: MutableMap<String, Int> = mutableMapOf(),
    cycles: MutableList<List<String>> = mutableListOf(),
    parents: MutableMap<String, String> = mutableMapOf(),
) {
    val color = colors[node]
    if (color == 2) {
        return
    }

    if (color == 1) {
        val cycle = mutableListOf<String>()
        var cur = parent
        cycle.add(parent)

        while (cur != node) {
            cur = parents.getValue(cur)
            cycle.add(cur)
        }
        cycles.add(cycle)
        return
    }

    parents[node] = parent
    colors[node] = 1

    val neighbors = getValue(node)
    for (neighbor in neighbors) {
        if (neighbor == parents[node]) {
            continue
        }
        dfsCycle(neighbor, node, colors, cycles, parents)
    }

    colors[node] = 2
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
