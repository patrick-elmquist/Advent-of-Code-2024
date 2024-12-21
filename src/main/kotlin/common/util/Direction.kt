package common.util

enum class Direction(
    val point: Point,
    val pointL: PointL,
) {
    Left(Point(-1, 0), PointL(-1L, 0L)),
    Up(Point(0, -1), PointL(0L, -1L)),
    Right(Point(1, 0), PointL(1L, 0L)),
    Down(Point(0, 1), PointL(0L, 1L));

    companion object
}

fun Direction.Companion.of(char: Char): Direction = when (char) {
    '^' -> Direction.Up
    'v' -> Direction.Down
    '<' -> Direction.Left
    '>' -> Direction.Right
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
