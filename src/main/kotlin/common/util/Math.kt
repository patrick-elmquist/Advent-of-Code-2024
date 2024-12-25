package common.util

import kotlin.math.pow

fun Int.isBitSet(index: Int): Boolean = (this shr index) and 1 != 0

fun Int.getBit(index: Int): Int = if (isBitSet(index)) 1 else 0

fun Int.pow(n: Int): Int = this.toFloat().pow(n).toInt()
fun Long.pow(n: Int): Long = this.toFloat().pow(n).toLong()

fun leastCommonMultiple(a: Int, b: Int): Int {
    return a * b / greatestCommonDivisor(a, b)
}

fun leastCommonMultiple(a: Long, b: Long): Long {
    return a * b / greatestCommonDivisor(a, b)
}

tailrec fun greatestCommonDivisor(a: Int, b: Int): Int {
    return if (b == 0) a
    else greatestCommonDivisor(b, a % b)
}

tailrec fun greatestCommonDivisor(a: Long, b: Long): Long {
    return if (b == 0L) a
    else greatestCommonDivisor(b, a % b)
}
