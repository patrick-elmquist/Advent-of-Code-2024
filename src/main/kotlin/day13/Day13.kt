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
            parseMachineData(input).sumOf(::calculateTokens)
        }
        verify {
            expect result 33481L
            run test 1 expect 480L
        }

        part2 { input ->
            val extra = PointL(10000000000000L, 10000000000000L)
            parseMachineData(input, extra).sumOf(::calculateTokens)
        }
        verify {
            expect result 92572057880885L
        }
    }
}

private fun calculateTokens(data: MachineData): Long {
    val (ax, ay) = data.a
    val (bx, by) = data.b
    val (x, y) = data.target
    val b = (ax * y - ay * x) / (by * ax - bx * ay)
    val a = (x - b * bx) / ax
    val calculatedTarget = PointL(a * ax + b * bx, a * ay + b * by)
    return if (calculatedTarget == data.target) a * 3 + b else 0
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
