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
            var robot = map.findRobot()

            val boxChars = setOf('O')
            instructions.forEach { direction ->
                val next = robot.nextInDirection(direction)
                val moveRobot = when (map[next]) {
                    null -> true
                    '#' -> false
                    'O' -> map.canMoveInDirection(robot, direction, boxChars)
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
            var robot = map.findRobot()

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

private fun MutableMap<Point, Char>.findRobot(): Point =
    entries.first { it.value == '@' }.key

private fun MutableMap<Point, Char>.canMoveInDirection(
    point: Point,
    direction: Direction,
    lookFor: Set<Char>,
): Boolean {
    var newNext = point.nextInDirection(direction)
    val movableBoxes = buildList {
        while (get(newNext) in lookFor) {
            add(newNext)
            newNext = newNext.nextInDirection(direction)
        }
    }

    when (get(newNext)) {
        '#' -> return false
        null -> moveBoxes(movableBoxes, direction)
        else -> error("")
    }

    return true
}

private fun MutableMap<Point, Char>.canMoveVertically(
    robot: Point,
    direction: Direction,
): Boolean = findMovableBoxes(robot, direction)
    .also { boxes -> moveBoxes(boxes, direction) }
    .isNotEmpty()

private fun MutableMap<Point, Char>.findMovableBoxes(
    point: Point,
    direction: Direction,
): List<Point> {
    val next = point.nextInDirection(direction)
    when (this[next]) {
        null -> return listOf(point)
        '#' -> return emptyList()
        else -> {
            val (left, right) = getBoxPair(next)

            val leftResult = findMovableBoxes(left, direction).takeIf { it.isNotEmpty() }
                ?: return emptyList()

            val rightResult = findMovableBoxes(right, direction).takeIf { it.isNotEmpty() }
                ?: return emptyList()

            return leftResult + left + rightResult + right
        }
    }
}

private fun MutableMap<Point, Char>.moveRobot(from: Point, to: Point): Point {
    remove(from)
    put(to, '@')
    return to
}

private fun MutableMap<Point, Char>.moveBoxes(
    boxesToMove: List<Point>,
    direction: Direction,
) {
    val movedBoxes = boxesToMove.map { it.nextInDirection(direction) to getValue(it) }
    boxesToMove.map { remove(it) }
    putAll(movedBoxes)
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
