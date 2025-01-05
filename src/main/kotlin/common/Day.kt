@file:Suppress("NOTHING_TO_INLINE")

package common

import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.measureTimedValue

typealias Solver = (Input) -> Any?

fun day(
    n: Int,
    block: Sheet.() -> Unit,
) = runBlocking {
    if (makeSureInputFileIsAvailable(day = n)) {
        collectSolutions(n, block).verifyAndRun(input = Input(day = n))
    } else {
        println("Input file is not available")
    }
}

private inline fun collectSolutions(
    day: Int,
    block: Sheet.() -> Unit,
) = Sheet(day = day).apply(block)

private inline fun Sheet.verifyAndRun(input: Input) {
    println("Day $day")
    val hasTests = parts.any { it.config.tests.isNotEmpty() }
    var emojiString = StringBuilder()
    parts.forEachIndexed { i, (solution, config) ->
        val n = i + 1

        if (config.ignore) {
            println("[IGNORING] Part $n")
            emojiString.append(Emoji.DID_NOT_RUN)
            return@forEachIndexed
        }

        val tests = config.tests
        if (tests.isNotEmpty()) println("Verifying Part #$n")
        val testOk = tests.all { test ->
            val result = solution.runWithTimer(test.input)
            outputTestResult(test, result)
            result.output == test.expected
        }

        print("answer #$n: ")
        when {
            !testOk -> {
                println("One or more tests failed.")
                emojiString.append(Emoji.FAIL)
            }

            config.breakAfterTest -> {
                println("Break added")
                emojiString.append(Emoji.DID_NOT_RUN)
            }

            else -> {
                try {
                    val result = solution.runWithTimer(input = input)
                    when {
                        config.expected == null -> {
                            println("${result.output} (${result.time.toMillisString()})")
                            emojiString.append(Emoji.UNKNOWN)
                        }

                        result.output == config.expected -> {
                            println("${result.output} (${result.time.toMillisString()})")
                            emojiString.append(Emoji.STAR)
                        }

                        else -> {
                            println("${Emoji.FAIL} FAIL Expected:${config.expected} actual:${result.output}")
                            emojiString.append(Emoji.FAIL)
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    println(e.message)
                    emojiString.append(Emoji.FAIL)
                }
            }
        }

        if (hasTests) println()
    }

    println(emojiString)
}

private fun outputTestResult(test: Test, result: Answer) {
    val testPassed = result.output == test.expected
    val message = if (testPassed) {
        "[PASS ${result.time.toMillisString()}]"
    } else {
        "[${Emoji.FAIL} FAIL]"
    }
    println("$message Input: ${test.input.lines}")
    if (!testPassed) {
        println("Expected: ${test.expected}")
        println("Actual: ${result.output}")
    }
}

private inline fun Solver.runWithTimer(input: Input) =
    measureTimedValue { invoke(input) }
        .let { result -> Answer(result.value, result.duration) }

private fun Duration.toMillisString() = "${inWholeMilliseconds}ms"

private object Emoji {
    const val STAR = "⭐️"
    const val UNKNOWN = "❔"
    const val DID_NOT_RUN = "🚫"
    const val FAIL = "❌"
}
