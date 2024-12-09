package day09

import common.Input
import common.day

// answer #1: 6332189866718
// answer #2: 6353648390778

private sealed interface Space {
    val size: Int
    val offset: Int

    data class File(override val size: Int, val id: Int, override val offset: Int) : Space {
        fun sum(): Long {
            var sum = 0L
            for (i in offset..< offset + size) {
                sum += i * id
            }
            return sum
        }
    }

    data class Empty(override val size: Int, override val offset: Int) : Space
}

fun main() {
    day(n = 9) {
        part1 { input ->
            val array = ArrayDeque(parseInput(input))

            var currentFile: Space.File? = null
            val list = mutableListOf<Space.File>()
            while (array.isNotEmpty()) {
                val next = array.removeFirst()

                when (next) {
                    is Space.File -> list += next
                    is Space.Empty -> {
                        var empty: Space.Empty = next

                        while (empty.size > 0) {
                            currentFile = currentFile ?: findLastFile(array)

                            if (currentFile == null) break

                            val fileSize = currentFile.size
                            val offset = empty.offset
                            val availableSpace = empty.size

                            if (fileSize <= availableSpace) {
                                list += currentFile.copy(offset = offset)
                                empty = empty.copy(
                                    offset = offset + fileSize,
                                    size = availableSpace - fileSize,
                                )
                                currentFile = null
                            } else {
                                list += currentFile.copy(size = availableSpace, offset = offset)
                                empty = empty.copy(size = 0)
                                currentFile = currentFile.copy(size = fileSize - availableSpace)
                            }
                        }
                    }
                }
            }
            list.sumOf { it.sum() } + (currentFile?.sum() ?: 0)
        }
        verify {
            expect result 6332189866718L
            run test 1 expect 1928L
        }

        part2 { input ->
            var array = ArrayDeque(parseInput(input))
            val list = mutableListOf<Space.File>()
            while(array.isNotEmpty()) {
                val next = array.removeLast()
                when (next) {
                    is Space.File -> {
                        val index = array.indexOfFirst { space ->
                            when (space) {
                                is Space.Empty -> space.size >= next.size
                                is Space.File -> false
                            }
                        }

                        if (index > -1) {
                            val empty = array[index] as Space.Empty
                            if (empty.size == next.size) {
                                array.removeAt(index)
                                list += next.copy(offset = empty.offset)
                            } else {
                                array[index] = empty.copy(
                                    size = empty.size - next.size,
                                    offset = empty.offset + next.size,
                                )
                                list += next.copy(offset = empty.offset)
                            }
                        } else {
                            list += next
                        }
                    }
                    is Space.Empty -> continue
                }
            }
            list.sumOf { it.sum() }
        }
        verify {
            expect result 6353648390778L
            run test 1 expect 2858L
        }
    }
}

private fun findLastFile(array: ArrayDeque<Space>): Space.File? =
    when (val next = array.removeLastOrNull()) {
        null -> null
        is Space.Empty -> array.removeLastOrNull() as? Space.File
        is Space.File -> next
    }

private fun parseInput(input: Input): List<Space> {
    val line = input.lines.first()
    var offset = 0
    return line.map { it.digitToInt() }.mapIndexed { index, n ->
        if (index % 2 == 0) {
            Space.File(size = n, id = index / 2, offset = offset)
        } else {
            Space.Empty(size = n, offset = offset)
        }.also { offset += n }
    }
}
