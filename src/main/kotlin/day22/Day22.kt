@file:Suppress("NOTHING_TO_INLINE")

package day22

import common.day
import common.util.log
import kotlinx.serialization.descriptors.setSerialDescriptor

// answer #1: 16999668565
// answer #2:

private val tenIterations = listOf(
    15887950,
    16495136,
    527345,
    704524,
    1553684,
    12683156,
    11100544,
    12249484,
    7753432,
    5908254,
)

private val tenPrices = listOf(
    3L,
    0L,
    6L,
    5L,
    4L,
    4L,
    6L,
    4L,
    4L,
    2L,
)

inline fun mix(a: Long, b: Long): Long = a xor b
inline fun prune(a: Long): Long = a % 16777216L
inline fun price(a: Long): Long = a % 10L
inline fun nextSecret(old: Long): Long {
    var secret = prune(mix(old, 64L * old))
    secret = prune(mix(secret, (secret / 32f).toLong()))
    secret = prune(mix(secret, secret * 2048L))
    return secret
}

private fun findRepeatingSequence(map: Map<List<Long>, Long>): Int {
    val list = map.toList()
    val size = list.size / 2






    TODO()
}

fun main() {
    day(n = 22) {
        part1 { input ->
            var i = 123L
            val test = buildList {
                repeat(10) {
                    i = nextSecret(i)
                    add(i)
                }
            }

            check(test == tenIterations.map(Int::toLong)) { "$test\n$tenIterations" }
            input.lines.map(String::toLong)
                .sumOf { initial ->
                    var secret = initial
                    repeat(2000) {
                        secret = nextSecret(secret)
                    }
                    secret
                }
        }
        verify {
            expect result 16999668565L
            run test 1 expect 37327623L
        }

        part2 { input ->
            val sellers = input.lines.map(String::toLong)

            val allSequences = sellers.asSequence()
                .map { seller ->
                    seller.log("seller")
                    seller to buildList {
                        var secret = seller
                        add(secret)
                        repeat(2000) {
                            secret = nextSecret(secret)
                            add(secret)
                        }
                    }
                        .map(::price)
                        .windowed(5).associate { it.zipWithNext { a, b -> b - a } to it.last() }
                }.toList()

            allSequences.map { (seller, cache) ->
                seller.log()
                cache.log()
                println()
            }

        }
        verify {
            breakAfterTest()
            expect result null
            run test 2 expect Unit
        }
    }
}
