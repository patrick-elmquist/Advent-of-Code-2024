package common.util

import kotlin.math.abs

typealias Vec2i = Point

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
        val Origin = Zero
    }
}

fun Point(xy: List<Int>): Point {
    require(xy.size == 2)
    return Point(xy[0], xy[1])
}

fun Point(x: String, y: String) =
    Point(x.toInt(), y.toInt())

fun Point(match: MatchResult.Destructured) =
    match.let { (x, y) -> Point(x.toInt(), y.toInt()) }

operator fun Point.plus(dir: Direction) = this + dir.point
operator fun Point.minus(dir: Direction) = this - dir.point

val Point.leftNeighbour: Point
    get() = this + Direction.Left

val Point.rightNeighbour: Point
    get() = this + Direction.Right

val Point.aboveNeighbour: Point
    get() = this + Direction.Up

val Point.belowNeighbour: Point
    get() = this + Direction.Down

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
