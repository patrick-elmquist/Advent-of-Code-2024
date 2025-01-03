@file:Suppress("NOTHING_TO_INLINE")

package common

import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.measureTimedValue

typealias Solver = (Input) -> Any?

fun day(
    n: Int,
    block: Sheet.() -> Unit
) = runBlocking {
    if (makeSureInputFileIsAvailable(day = n)) {
        collectSolutions(n, block).verifyAndRun(input = Input(day = n))
    } else {
        println("Input file is not available")
    }
}

private inline fun collectSolutions(
    day: Int,
    block: Sheet.() -> Unit
) = Sheet(day = day).apply(block)

private inline fun Sheet.verifyAndRun(input: Input) {
    println("Day $day")
    val hasTests = parts.any { it.config.tests.isNotEmpty() }
    var passCount = 0
    var emojiString = StringBuilder()
    parts.forEachIndexed { i, (solution, config) ->
        val n = i + 1

        if (config.ignore) {
            println("[IGNORING] Part $n")
            emojiString.append("🤐")
            return@forEachIndexed
        }

        val result = solution.evaluate(
            n = n,
            input = input,
            expected = config.expected,
            testOnly = config.breakAfterTest,
            tests = config.tests,
        )
        print("answer #$n: ")
        result
            .onSuccess {
                println("${it.output} (${it.time.toMillisString()})")
                passCount++
                emojiString.append("⭐️")
            }
            .onFailure {
                println(it.message)
                emojiString.append("❌")
            }
        if (hasTests) println()
    }

    println(emojiString)
}

private inline fun Solver.evaluate(
    n: Int,
    input: Input,
    expected: Any?,
    testOnly: Boolean,
    tests: List<Test>
): Result<Answer> {
    if (tests.isNotEmpty()) println("Verifying Part #$n")

    val testsPassed = tests.all {
        val testInput = it.input
        val result = runWithTimer(testInput)
        val testPassed = result.output == it.expected

        print("[${if (testPassed) "PASS ${result.time.toMillisString()}" else "FAIL"}]")
        print(" Input: ${testInput.lines}")
        println()
        testPassed.also { passed ->
            if (!passed) {
                println("Expected: ${it.expected}")
                println("Actual: ${result.output}")
            }
        }
    }

    return when {
        !testsPassed -> failure("One or more tests failed.")
        testOnly -> failure("Break added")
        else -> try {
            val result = runWithTimer(input)
            if (expected == null || result.output == expected) {
                success(result)
            } else {
                failure("FAIL Expected:$expected actual:${result.output}")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            failure(e)
        }
    }
}

private inline fun Solver.runWithTimer(input: Input) =
    measureTimedValue { invoke(input) }
        .let { result -> Answer(result.value, result.duration) }

private inline fun success(answer: Answer) = Result.success(answer)
private inline fun failure(message: String) = Result.failure<Answer>(AssertionError(message))
private inline fun failure(throwable: Throwable) = Result.failure<Answer>(throwable)

private fun Duration.toMillisString() = "${inWholeMilliseconds}ms"
