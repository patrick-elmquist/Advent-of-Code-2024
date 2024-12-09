package day09

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
            val line = input.lines.first()
            var offset = 0
            val parsed = line.map { it.digitToInt() }.mapIndexed { index, n ->
                if (index % 2 == 0) {
                    Space.File(size = n, id = index / 2, offset = offset)
                } else {
                    Space.Empty(size = n, offset = offset)
                }.also { offset += n }
            }
            val array = ArrayDeque(parsed)

            fun findLastFile(array: ArrayDeque<Space>): Space.File? {
                val next = array.removeLastOrNull()
                return when (next) {
                    null -> null
                    is Space.Empty -> array.removeLastOrNull() as? Space.File
                    is Space.File -> next
                }
            }

            var sum = 0L
            var currentFile: Space.File? = null
            while (array.isNotEmpty()) {
                val next = array.removeFirst()

                when (next) {
                    is Space.File -> sum += next.sum()
                    is Space.Empty -> {
                        var empty: Space.Empty = next
                        while (empty.size > 0) {
                            currentFile = currentFile ?: findLastFile(array)

                            if (currentFile == null) break

                            val fileSize = currentFile.size

                            if (fileSize == empty.size) {
                                sum += currentFile.copy(offset = empty.offset).sum()
                                currentFile = null
                                break
                            } else if (fileSize < empty.size) {
                                // whole file goes in the empty space
                                sum += currentFile.copy(offset = empty.offset).sum()
                                empty = empty.copy(
                                    offset = empty.offset + fileSize,
                                    size = empty.size - currentFile.size,
                                )
                                currentFile = null
                            } else {
                                sum += currentFile.copy(
                                    size = empty.size,
                                    offset = empty.offset,
                                ).sum()
                                currentFile = currentFile.copy(size = currentFile.size - empty.size)
                                break
                            }
                        }
                    }
                }
            }
            sum += currentFile?.sum() ?: 0
            sum
        }
        verify {
            expect result 6332189866718L
            run test 1 expect 1928L
        }

        part2 { input ->
            val line = input.lines.first()
            var offset = 0
            val parsed = line.map { it.digitToInt() }.mapIndexed { index, n ->
                if (index % 2 == 0) {
                    Space.File(size = n, id = index / 2, offset = offset)
                } else {
                    Space.Empty(size = n, offset = offset)
                }.also { offset += n }
            }

            var array = ArrayDeque(parsed)
            var sum = 0L
            var list = mutableListOf<Space.File>()
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
                                list.add(next.copy(offset = empty.offset))
                                sum += next.copy(offset = empty.offset).sum()
                            } else {
                                array[index] = empty.copy(
                                    size = empty.size - next.size,
                                    offset = empty.offset + next.size,
                                )
                                list.add(next.copy(offset = empty.offset))
                                sum += next.copy(offset = empty.offset).sum()
                            }
                        } else {
                            list.add(next)
                            sum += next.sum()
                        }
                    }

                    is Space.Empty -> continue
                }
            }

            sum

        }
        verify {
            expect result 6353648390778L
            run test 1 expect 2858L
        }
    }
}
