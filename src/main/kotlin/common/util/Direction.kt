package common.util

enum class Direction(val point: Point) {
    Left(Point(-1, 0)),
    Up(Point(0, -1)),
    Right(Point(1, 0)),
    Down(Point(0, 1));

    val pointL = PointL(point.x, point.y)

    companion object
}

fun Direction.Companion.of(char: Char): Direction = when (char) {
    '^','u','U','n','N' -> Direction.Up
    'v','d','D','s','S' -> Direction.Down
    '<','l','L','w','W'-> Direction.Left
    '>','r','R','e','E' -> Direction.Right
    else -> error("Can't create direction from '$char'")
}

val Direction.isHorizontal
    get() = this == Direction.Left || this == Direction.Right

val Direction.isVertical
    get() = this == Direction.Up || this == Direction.Down

val Direction.opposite
    get() = when (this) {
        Direction.Left -> Direction.Right
        Direction.Up -> Direction.Down
        Direction.Right -> Direction.Left
        Direction.Down -> Direction.Up
    }

val Direction.nextCW: Direction
    get() = when (this) {
        Direction.Left -> Direction.Up
        Direction.Up -> Direction.Right
        Direction.Right -> Direction.Down
        Direction.Down -> Direction.Left
    }

val Direction.nextCCW: Direction
    get() = when (this) {
        Direction.Left -> Direction.Down
        Direction.Up -> Direction.Left
        Direction.Right -> Direction.Up
        Direction.Down -> Direction.Right
    }
