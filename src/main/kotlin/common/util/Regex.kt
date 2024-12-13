package common.util

fun Regex.match(string: String) = matchEntire(string)!!.destructured
