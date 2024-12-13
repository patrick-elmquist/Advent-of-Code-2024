package day13

import common.Input
import common.day
import common.util.PointL
import common.util.sliceByBlank

// answer #1: 33481
// answer #2: 92572057880885

fun main() {
    day(n = 13) {
        part1 { input ->
            parseMachineData(input).sumOf(::calculateToken)
        }
        verify {
            expect result 33481L
            run test 1 expect 480L
        }

        part2 { input ->
            val extra = PointL(10000000000000L, 10000000000000L)
            parseMachineData(input, extra).sumOf(::calculateToken)
        }
        verify {
            expect result 92572057880885L
        }
    }
}

private fun calculateToken(data: MachineData): Long {
    val a = data.a
    val b = data.b
    val (x, y) = data.target
    val bPresses = (a.x * y - a.y * x) / (b.y * a.x - b.x * a.y)
    val aPresses = (x - bPresses * b.x) / a.x
    return when {
        x != aPresses * a.x + bPresses * b.x -> 0
        y != aPresses * a.y + bPresses * b.y -> 0
        else -> aPresses * 3 + bPresses
    }
}

private data class MachineData(val a: PointL, val b: PointL, val target: PointL)

private val buttonRegex = """Button [AB]: X\+(\d+), Y\+(\d+)""".toRegex()
private val prizeRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()

private fun parseMachineData(input: Input, extra: PointL = PointL(0L, 0L)) =
    input.lines.sliceByBlank().map { (a, b, t) ->
        MachineData(
            buttonRegex.matchEntire(a)!!.destructured.let(::PointL),
            buttonRegex.matchEntire(b)!!.destructured.let(::PointL),
            prizeRegex.matchEntire(t)!!.destructured.let(::PointL) + extra,
        )
    }
