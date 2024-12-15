package day15

import common.day
import common.util.Direction
import common.util.Point
import common.util.from
import common.util.grid
import common.util.isHorizontal
import common.util.leftNeighbour
import common.util.nextInDirection
import common.util.rightNeighbour
import common.util.sliceByBlank

// answer #1: 1294459
// answer #2: 1319212

fun main() {
    day(n = 15) {
        part1 { input ->
            val (map, instructions) = input.lines.sliceByBlank().let { (a, b) ->
                a.grid.filterValues { it != '.' }.toMutableMap() to
                        b.joinToString("").map(Direction::from)
            }
            var robot = map.entries.first { it.value == '@' }.key

            val boxChars = setOf('O')
            instructions.forEach { direction ->
                val next = robot.nextInDirection(direction)
                val moveRobot = when (map[next]) {
                    null -> true
                    '#' -> false
                    'O' -> {
                        map.canMoveInDirection(robot, direction, boxChars)
                    }
                    else -> error("")
                }
                if (moveRobot) robot = map.moveRobot(robot, next)
            }

            map.filterValues { it == 'O' }.keys.sumOf { (x, y) -> x + y * 100 }
        }
        verify {
            expect result 1294459
            run test 1 expect 2028
            run test 2 expect 10092
        }

        part2 { input ->
            val (map, instructions) = input.lines.sliceByBlank().let { (a, b) ->
                a.map(::expandMapRow).grid.filterValues { it != '.' }.toMutableMap() to
                        b.joinToString("").map(Direction::from)
            }
            var robot = map.entries.first { it.value == '@' }.key

            val boxChars = setOf('[', ']')
            instructions.forEach { direction ->
                val next = robot.nextInDirection(direction)
                val moveRobot = when (map[next]) {
                    null -> true
                    '#' -> false
                    '[', ']' ->
                        if (direction.isHorizontal) {
                            map.canMoveInDirection(robot, direction, boxChars)
                        } else {
                            map.canMoveVertically(robot, direction)
                        }

                    else -> error("")
                }
                if (moveRobot) robot = map.moveRobot(robot, next)
            }
            map.filterValues { it == '[' }.keys.sumOf { (x, y) -> x + y * 100 }
        }
        verify {
            expect result 1319212
            run test 2 expect 9021
        }
    }
}

private fun MutableMap<Point, Char>.moveRobot(from: Point, to: Point): Point {
    remove(from)
    put(to, '@')
    return to
}

private fun MutableMap<Point, Char>.canMoveInDirection(
    point: Point,
    direction: Direction,
    lookFor: Set<Char> = setOf('O')
): Boolean {
    val next = point.nextInDirection(direction)
    val line = mutableSetOf<Point>()
    var newNext = next
    while (this[newNext] in lookFor) {
        line += newNext
        newNext = newNext.nextInDirection(direction)
    }

    when (this[newNext]) {
        '#' -> return false
        null -> {
            val movedPositions = line.map { it.nextInDirection(direction) to getValue(it) }
            line.map { remove(it) }
            putAll(movedPositions)
        }
        else -> error("")
    }
    return true
}

private fun MutableMap<Point, Char>.canMoveVertically(
    robot: Point,
    direction: Direction,
): Boolean {
    val (left, right) = getBoxPair(robot.nextInDirection(direction))

    val leftResult = this.rec(left, direction)
    if (leftResult.isEmpty()) return false

    val rightResult = this.rec(right, direction)
    if (rightResult.isEmpty()) return false

    val oldPositions = leftResult + left + rightResult + right
    val newPositions = oldPositions.map { it.nextInDirection(direction) to getValue(it) }
    oldPositions.forEach { remove(it) }
    putAll(newPositions)
    return true
}

private fun MutableMap<Point, Char>.rec(point: Point, direction: Direction): List<Point> {
    val next = point.nextInDirection(direction)
    val value = this[next]

    if (value == null) return listOf(point)
    if (value == '#') return emptyList()

    val (left, right) = getBoxPair(next)

    val leftResult = rec(left, direction)
    if (leftResult.isEmpty()) return emptyList()

    val rightResult = rec(right, direction)
    if (rightResult.isEmpty()) return emptyList()

    return leftResult + rightResult + listOf(left, right)
}

private fun expandMapRow(row: String): String =
    row.map { value ->
        when (value) {
            '#' -> "##"
            'O' -> "[]"
            '.' -> ".."
            '@' -> "@."
            else -> error("")
        }
    }.joinToString("")

private fun MutableMap<Point, Char>.getBoxPair(side: Point): Pair<Point, Point> =
    when (this[side]) {
        '[' -> side to side.rightNeighbour
        ']' -> side.leftNeighbour to side
        else -> error("")
    }
