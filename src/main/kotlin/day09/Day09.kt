package day09

import common.day
import day09.Space.AvailableSpace
import day09.Space.File

// answer #1: 6332189866718
// answer #2: 6353648390778

fun main() {
    day(n = 9) {
        part1 { input ->
            val filesAndFreeSpace = parseAndExpandDiskMap(input.lines.first())
            compressFiles(filesAndFreeSpace).sumOf(::checksum)
        }
        verify {
            expect result 6332189866718L
            run test 1 expect 1928L
        }

        part2 { input ->
            val filesAndFreeSpace = parseAndExpandDiskMap(input.lines.first())
            moveFiles(filesAndFreeSpace).sumOf(::checksum)
        }
        verify {
            expect result 6353648390778L
            run test 1 expect 2858L
        }
    }
}

private fun compressFiles(input: List<Space>): MutableList<File> {
    val array = ArrayDeque(input)
    var file: File? = null
    val files = mutableListOf<File>()
    while (array.isNotEmpty()) {
        val item = array.removeFirst()

        when (item) {
            is File -> files += item
            is AvailableSpace -> {
                var space: AvailableSpace = item
                while (space.size > 0) {
                    file = file ?: findLastFile(array)
                    when {
                        file == null -> break

                        file.size <= space.size -> {
                            files += file.copy(offset = space.offset)
                            space = space.copy(
                                offset = space.offset + file.size,
                                size = space.size - file.size,
                            )
                            file = null
                        }

                        else -> {
                            files += file.copy(size = space.size, offset = space.offset)
                            file = file.copy(size = file.size - space.size)
                            space = space.copy(size = 0)
                        }
                    }
                }
            }
        }
    }
    file?.let { files += it }
    return files
}

private fun moveFiles(filesAndSpace: List<Space>): MutableList<File> {
    var array = ArrayDeque(filesAndSpace)
    val files = mutableListOf<File>()
    while (array.isNotEmpty()) {
        val file = array.removeLast()
        when (file) {
            is File -> {
                files += when (val index = array.indexOfAvailableSpace(file)) {
                    -1 -> file
                    else -> {
                        val availableSpace = array[index] as AvailableSpace
                        if (availableSpace.size == file.size) {
                            array.removeAt(index)
                            file.copy(offset = availableSpace.offset)
                        } else {
                            array[index] = availableSpace.copy(
                                size = availableSpace.size - file.size,
                                offset = availableSpace.offset + file.size,
                            )
                            file.copy(offset = availableSpace.offset)
                        }
                    }
                }
            }

            is AvailableSpace -> continue
        }
    }
    return files
}

private fun ArrayDeque<Space>.indexOfAvailableSpace(next: File): Int =
    indexOfFirst { space ->
        when (space) {
            is AvailableSpace -> space.size >= next.size
            is File -> false
        }
    }

private fun findLastFile(array: ArrayDeque<Space>): File? =
    when (val next = array.removeLastOrNull()) {
        null -> null
        is AvailableSpace -> array.removeLastOrNull() as? File
        is File -> next
    }

private fun parseAndExpandDiskMap(diskMap: String): List<Space> {
    var offset = 0
    return diskMap
        .map { it.digitToInt() }
        .mapIndexed { index, n ->
            if (index % 2 == 0) {
                File(size = n, offset = offset, id = index / 2)
            } else {
                AvailableSpace(size = n, offset = offset)
            }.also { offset += n }
        }
}

private fun checksum(file: File): Long =
    (file.offset.toLong()..<file.offset.toLong() + file.size).sumOf { it * file.id }

private sealed interface Space {
    data class File(val size: Int, val offset: Int, val id: Int) : Space
    data class AvailableSpace(val size: Int, val offset: Int) : Space
}
