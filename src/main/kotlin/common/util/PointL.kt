package common.util

import kotlin.math.abs

typealias Vec2l = PointL

data class PointL(val x: Long, val y: Long) : Comparable<PointL> {
    operator fun plus(point: PointL) = PointL(x + point.x, y + point.y)
    operator fun minus(point: PointL) = PointL(x - point.x, y - point.y)

    override fun compareTo(other: PointL): Int {
        val y = y.compareTo(other.y)
        return if (y == 0) {
            x.compareTo(other.x)
        } else {
            y
        }
    }

    companion object {
        val Zero = PointL(0L, 0L)
        val Origin = Zero
    }
}

fun PointL(xy: List<Long>): PointL {
    require(xy.size == 2)
    return PointL(xy[0], xy[1])
}

fun PointL(x: Int, y: Int) =
    PointL(x.toLong(), y.toLong())

fun PointL(x: String, y: String) =
    PointL(x.toLong(), y.toLong())

fun PointL(match: MatchResult.Destructured) =
    match.let { (x, y) -> PointL(x.toLong(), y.toLong()) }

operator fun PointL.plus(dir: Direction) = this + dir.pointL
operator fun PointL.minus(dir: Direction) = this - dir.pointL

fun PointL.distance(other: PointL): Long =
    abs(other.x - x) + abs(other.y - y)

