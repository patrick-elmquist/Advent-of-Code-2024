package common.util

import kotlin.math.abs

/**
 * Class representing a point in 2 dimensions
 */
data class Point(val x: Int, val y: Int): Comparable<Point> {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    override fun compareTo(other: Point): Int {
        val y = y.compareTo(other.y)
        return if (y == 0) {
            x.compareTo(other.x)
        } else {
            y
        }
    }

    companion object {
        val Zero = Point(0, 0)
    }
}

fun Point(xy: List<Int>) = Point(xy[0].toInt(), xy[1].toInt())
fun Point(x: String, y: String) = Point(x.toInt(), y.toInt())
fun Point(match: MatchResult.Destructured) = match.let { (x, y) -> Point(x.toInt(), y.toInt()) }


data class PointL(val x: Long, val y: Long) {
    operator fun plus(point: PointL) = PointL(x + point.x, y + point.y)
    operator fun minus(point: PointL) = PointL(x - point.x, y - point.y)

    companion object
}

fun PointL(x: String, y: String) = PointL(x.toLong(), y.toLong())
fun PointL(match: MatchResult.Destructured) = match.let { (x, y) -> PointL(x.toLong(), y.toLong()) }

data class Point3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    companion object
}

fun Point3D(x: String, y: String, z: String) = Point3D(x.toInt(), y.toInt(), z.toInt())
fun Point3D(match: MatchResult.Destructured) = match.let { (x, y, z) ->
    Point3D(x.toInt(), y.toInt(), z.toInt())
}

data class PointL3D(val x: Long, val y: Long, val z: Long) {
    operator fun plus(point: PointL3D) = PointL3D(x + point.x, y + point.y, z + point.z)
    operator fun minus(point: PointL3D) = PointL3D(x - point.x, y - point.y, z - point.z)

    companion object
}

fun PointL3D(x: String, y: String, z: String) = PointL3D(x.toLong(), y.toLong(), z.toLong())
fun PointL3D(match: MatchResult.Destructured) = match.let { (x, y, z) ->
    PointL3D(x.toLong(), y.toLong(), z.toLong())
}

val Point3D.xy: Point
    get() = Point(x = x, y = y)

val Point.leftNeighbour: Point
    get() = copy(x = x - 1)

val Point.rightNeighbour: Point
    get() = copy(x = x + 1)

val Point.aboveNeighbour: Point
    get() = copy(y = y - 1)

val Point.belowNeighbour: Point
    get() = copy(y = y + 1)

fun Point.distance(other: Point): Int =
    abs(other.x - x) + abs(other.y - y)

fun Point.nextInDirection(direction: Direction, steps: Int = 1): Point =
    when (direction) {
        Direction.Left -> copy(x = x - steps)
        Direction.Up -> copy(y = y - steps)
        Direction.Right -> copy(x = x + steps)
        Direction.Down -> copy(y = y + steps)
    }

fun Point.neighbors(
    diagonal: Boolean = false,
    includeSelf: Boolean = false,
) = sequence {
    if (diagonal) yield(Point(x - 1, y - 1))
    yield(copy(y = y - 1))
    if (diagonal) yield(Point(x + 1, y - 1))

    yield(copy(x = x - 1))
    if (includeSelf) yield(this@neighbors)
    yield(copy(x = x + 1))

    if (diagonal) yield(Point(x - 1, y + 1))
    yield(copy(y = y + 1))
    if (diagonal) yield(Point(x + 1, y + 1))
}

fun Point3D.neighbors() = sequence {
    yield(copy(y = y - 1))
    yield(copy(y = y + 1))

    yield(copy(x = x - 1))
    yield(copy(x = x + 1))

    yield(copy(z = z - 1))
    yield(copy(z = z + 1))
}

