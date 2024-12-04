package day04

import common.day
import common.util.log
import kotlin.math.min

// answer #1: 2654
// answer #2:

private fun assert(condition: Boolean) {
    if (!condition) error("")
}

fun main() {
    day(n = 4) {
        part1 { input ->
            val lines = input.lines
            val width = lines.first().length
            val height = lines.size

            var counter = 0
            for (y in 0..<height) {
                for (x in 0..<width) {
                    if (lines.findVertical(x, y)) {
                        counter++
                    }
                    if (lines.findHorizontal(x, y)) {
                        counter++
                    }
                    val findDiagonal = lines.findDiagonal(x, y)
                    if (findDiagonal > 0) {
                        counter += findDiagonal
                    }
                }
            }
            counter
        }
        verify {
            expect result 2654
            run test 1 expect 18
        }

        part2 { input ->
            val lines = input.lines
            val width = lines.first().length
            val height = lines.size

            var counter = 0
            for (y in 0..<height) {
                for (x in 0..<width) {
                    val findDiagonal = lines.findX(x, y)
                    if (findDiagonal == 2) {
                        log("found diagonal x:$x y:$y")
                        counter += 1
                    }
                }
            }
            counter
        }
        verify {
            expect result null
            run test 1 expect 9
        }
    }
}

private fun List<String>.findHorizontal(x: Int, y: Int): Boolean {
    val line = get(y)
    if (line.length < x + 4) return false
    val endIndex = x + 4
    val substring = line.substring(startIndex = x, endIndex = endIndex)
    return checkString(substring)
}

private fun List<String>.findVertical(x: Int, y: Int): Boolean {
    if (lastIndex < y + 3) return false
    val min = y + 3
    val string = buildString {
        for (i in y..min) {
            val line = this@findVertical[i]
            append(line[x])
        }
    }
    return checkString(string)
}

private fun List<String>.findDiagonal(x: Int, y: Int): Int {
    val xRange = first().indices
    val yRange = indices

    val coords1 = listOf(
        x to y,
        x + 1 to y + 1,
        x + 2 to y + 2,
        x + 3 to y + 3,
    )

    var count = 0
    if (coords1.all { (x, y) -> x in xRange && y in yRange }) {
        val string = buildString {
            coords1.forEach { (x, y) ->
                append(this@findDiagonal.get(y)[x])
            }
        }
        val checked = checkString(string)
        if (checked) count++
    }

    val coords2 = listOf(
        x to y,
        x - 1 to y + 1,
        x - 2 to y + 2,
        x - 3 to y + 3,
    )

    if (coords2.all { (x, y) -> x in xRange && y in yRange }) {
        val string = buildString {
            coords2.forEach { (x, y) ->
                append(this@findDiagonal.get(y)[x])
            }
        }
        val checked = checkString(string)
        if (checked) count++
    }

    return count
}

private fun checkXString(substring: String): Boolean {
    return (substring.length == 3 && (substring == "MAS" || substring.reversed() == "MAS"))
}

private fun List<String>.findX(x: Int, y: Int): Int {
    val xRange = first().indices
    val yRange = indices

    val coords1 = listOf(
        x - 1 to y - 1,
        x to y,
        x + 1 to y + 1,
    )

    var count = 0
    if (coords1.all { (x, y) -> x in xRange && y in yRange }) {
        val string = buildString {
            coords1.forEach { (x, y) ->
                append(this@findX[y][x])
            }
        }
        string.log()
        val checked = checkXString(string)
        if (checked) count++
    }

    val coords2 = listOf(
        x + 1 to y - 1,
        x to y,
        x - 1 to y + 1,
    )

    if (coords2.all { (x, y) -> x in xRange && y in yRange }) {
        val string = buildString {
            coords2.forEach { (x, y) ->
                append(this@findX[y][x])
            }
        }
//        string.log()
        val checked = checkXString(string)
        if (checked) count++
    }

    return count
}

private fun checkString(substring: String): Boolean {
    return (substring.length == 4 && (substring == "XMAS" || substring.reversed() == "XMAS"))
}
