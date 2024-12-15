package day15

import common.day
import common.util.Direction
import common.util.Point
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
            val sliced = input.lines.sliceByBlank()
            val grid = sliced.first().grid.filterValues { it != '.' }.toMutableMap()
            val instructions = sliced.last().joinToString("")
            var robot = grid.entries.first { it.value == '@' }.key

            fun tryMove(
                point: Point,
                direction: Direction,
                grid: MutableMap<Point, Char>,
            ): Boolean {
                // gather all linked O in the line
                // - if you reach # before . you can't do anything
                // - as soon as the line breaks to a ., move all in dir

                val next = point.nextInDirection(direction)
                val line = mutableSetOf<Point>()
                var newNext = next
                while (grid[newNext] == 'O') {
                    line += newNext
                    newNext = newNext.nextInDirection(direction)
                }

                val stopValue = grid[newNext]
                when (stopValue) {
                    '#' -> return false
                    null -> {
                        val movedPositions = line.map { it.nextInDirection(direction) } + newNext
                        line.map { grid.remove(it) }
                        movedPositions.forEach { grid[it] = 'O' }
                    }

                    else -> error("WAT?")
                }
                return true
            }

            instructions.forEach { instruction ->
                val dir = parseDirection(instruction)
                val next = robot.nextInDirection(dir)
                val nextValue = grid[next]

                var res: Boolean? = null
                when (nextValue) {
                    null -> {
                        grid.remove(robot)
                        robot = next
                        grid[robot] = '@'
                    }

                    '#' -> Unit
                    'O' -> {
                        val result = tryMove(robot, dir, grid)
                        if (result) {
                            grid.remove(robot)
                            robot = next
                            grid[robot] = '@'
                        }
                        res = result
                    }

                    else -> error("w00t")
                }
            }

            grid.entries.filter { it.value == 'O' }.sumOf { (key, _) -> key.x + key.y * 100 }
        }
        verify {
            expect result 1294459
            run test 1 expect 2028
            run test 2 expect 10092
        }

        part2 { input ->
            val sliced = input.lines.sliceByBlank()
            val grid = sliced.first().map { line ->
                line.map { value ->
                    when (value) {
                        '#' -> "##"
                        'O' -> "[]"
                        '.' -> ".."
                        '@' -> "@."
                        else -> error("")
                    }
                }.joinToString("")
            }.grid.filterValues { it != '.' }.toMutableMap()
            val instructions = sliced.last().joinToString("")
            var robot = grid.entries.first { it.value == '@' }.key

            instructions.forEachIndexed { i, instruction ->
                val dir = parseDirection(instruction)
                val next = robot.nextInDirection(dir)
                val nextValue = grid[next]

                var res: Boolean? = null
                when (nextValue) {
                    null -> {
                        grid.remove(robot)
                        robot = next
                        grid[robot] = '@'
                    }

                    '#' -> Unit
                    '[', ']' -> {
                        val result = if (dir.isHorizontal) {
                            tryMoveHorizontally(robot, dir, grid)
                        } else {
                            tryMoveVertically(robot, grid, dir)
                        }
                        if (result) {
                            grid.remove(robot)
                            robot = next
                            grid[robot] = '@'
                        }
                        res = result
                    }

                    else -> error("w00t")
                }
            }
            grid.entries.filter { it.value == '[' }.sumOf { (key, _) -> key.x + key.y * 100 }
        }
        verify {
            expect result 1319212
            run test 2 expect 9021
        }
    }
}

private fun tryMoveVertically(
    robot: Point,
    grid: MutableMap<Point, Char>,
    direction: Direction,
): Boolean {
    val next = robot.nextInDirection(direction)
    val box: Pair<Point, Point> = getBoxSides(next, direction, grid)
    val (left, right) = box

    val leftResult = rec(left, direction, grid)
    if (leftResult.isEmpty()) return false

    val rightResult = rec(right, direction, grid)
    if (rightResult.isEmpty()) return false

    val allLeft = leftResult + left
    val movedLeftPositions =
        allLeft.mapNotNull {
            val v = grid[it]
            if (v == null) {
                null
            } else {
                it.nextInDirection(direction) to v
            }
        }
    val allRight = rightResult + right
    val movedRightPositions =
        allRight.mapNotNull {
            val v = grid[it]
            if (v == null) {
                null
            } else {
                it.nextInDirection(direction) to v
            }
        }
    allLeft.forEach { grid.remove(it) }
    allRight.forEach { grid.remove(it) }
    movedLeftPositions.forEach { grid[it.first] = it.second }
    movedRightPositions.forEach { grid[it.first] = it.second }
    return true
}

fun rec(point: Point, direction: Direction, grid: MutableMap<Point, Char>): List<Point> {
    val next = point.nextInDirection(direction)
    val value = grid[next]

    if (value == null) return listOf(point)
    if (value == '#') return emptyList()

    val (left, right) = getBoxSides(next, direction, grid)

    val leftResult = rec(left, direction, grid)
    if (leftResult.isEmpty()) {
        return emptyList()
    }

    val rightResult = rec(right, direction, grid)
    if (rightResult.isEmpty()) {
        return emptyList()
    }

    return leftResult + rightResult + listOf(left, right)
}

private fun tryMoveHorizontally(
    robot: Point,
    direction: Direction,
    grid: MutableMap<Point, Char>,
): Boolean {
    val next = robot.nextInDirection(direction)
    val nextValue = grid[next]!!
    val line = mutableListOf<Pair<Point, Char>>(next to nextValue)
    var newNext = next.nextInDirection(direction)
    while (grid[newNext] == '[' || grid[newNext] == ']') {
        line += newNext to grid[newNext]!!
        newNext = newNext.nextInDirection(direction)
    }
    val stopValue = grid[newNext]
    when (stopValue) {
        '#' -> return false
        null -> {
            val movedPositions =
                line.map { it.first.nextInDirection(direction) to it.second }
            line.map { grid.remove(it.first) }
            movedPositions.forEach { (key, value) -> grid[key] = value }
        }

        else -> error("WAT?")
    }
    return true
}

private fun getBoxSides(
    next: Point,
    direction: Direction,
    grid: MutableMap<Point, Char>,
): Pair<Point, Point> = when (grid[next]) {
    '[' -> {
        when (direction) {
            Direction.Right,
            Direction.Down,
            Direction.Up,
                -> next to next.rightNeighbour

            Direction.Left -> error("")
        }
    }
    ']' -> {
        when (direction) {
            Direction.Right -> error("")
            Direction.Left,
            Direction.Up,
            Direction.Down,
                -> next.leftNeighbour to next
        }
    }
    else -> {
        error("")
    }
}

private fun parseDirection(instruction: Char): Direction = when (instruction) {
    '^' -> Direction.Up
    'v' -> Direction.Down
    '<' -> Direction.Left
    '>' -> Direction.Right
    else -> error("wth")
}
