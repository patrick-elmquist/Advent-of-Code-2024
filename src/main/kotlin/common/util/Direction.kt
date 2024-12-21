package common.util

enum class Direction {
    Left, Up, Right, Down;

    companion object
}

fun Direction.Companion.from(char: Char): Direction = when (char) {
    '^' -> Direction.Up
    'v' -> Direction.Down
    '<' -> Direction.Left
    '>' -> Direction.Right
    else -> error("Can't create direction from '$char'")
}

fun Direction.toPoint() = when(this) {
    Direction.Left -> Point(-1, 0)
    Direction.Up -> Point(0, -1)
    Direction.Right -> Point(1, 0)
    Direction.Down -> Point(0, 1)
}

val Direction.opposite
    get() = when (this) {
        Direction.Left -> Direction.Right
        Direction.Up -> Direction.Down
        Direction.Right -> Direction.Left
        Direction.Down -> Direction.Up
    }

val Direction.isHorizontal
    get() = this == Direction.Left || this == Direction.Right

val Direction.isVertical
    get() = this == Direction.Up || this == Direction.Down

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
