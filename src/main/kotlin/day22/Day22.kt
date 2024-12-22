@file:Suppress("NOTHING_TO_INLINE")

package day22

import common.day

// answer #1: 16999668565
// answer #2: 1898

fun main() {
    day(n = 22) {
        part1 { input ->
            val buyers = input.lines.map(String::toLong)
            buyers.sumOf { buyer -> buyerSequence(buyer).last() }
        }
        verify {
            expect result 16999668565L
            run test 1 expect 37327623L
        }

        part2 { input ->
            val buyers = input.lines.map(String::toLong)
            val buyersSequencesWithPrice = buyers.map { buyer ->
                buildMap {
                    val prices = buyerSequence(buyer).map(::price).toList()
                    for (i in 4..prices.lastIndex) {
                        val sequence = listOf(
                            prices[i - 3] - prices[i - 4],
                            prices[i - 2] - prices[i - 3],
                            prices[i - 1] - prices[i - 2],
                            prices[i] - prices[i - 1],
                        )
                        putIfAbsent(sequence, prices[i])
                    }
                }
            }

            val sequencesWithTotals = buildMap {
                buyersSequencesWithPrice.forEach { map ->
                    map.entries.forEach { (seq, price) ->
                        merge(seq, price, Long::plus)
                    }
                }
            }

            sequencesWithTotals.values.max()
        }
        verify {
            expect result 1898L
            run test 2 expect 23L
        }
    }
}

private fun buyerSequence(buyer: Long): Sequence<Long> =
    generateSequence(buyer) { nextSecret(it) }.take(2001)

private inline fun nextSecret(previous: Long): Long {
    var secret = previous
    secret = secret.mix(64L * secret).prune()
    secret = secret.mix(secret / 32L).prune()
    secret = secret.mix(secret * 2048L).prune()
    return secret
}

private inline fun Long.mix(b: Long): Long = this xor b
private inline fun Long.prune(): Long = this % 16777216L
private inline fun price(a: Long): Long = a % 10L
