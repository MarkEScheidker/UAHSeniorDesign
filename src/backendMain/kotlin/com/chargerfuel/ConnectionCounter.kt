package com.chargerfuel

object ConnectionCounter {
    private var counter = 0

    fun count(): String = getNumberSuffix(counter++)

    private fun getNumberSuffix(num: Int): String {
        return when(num.toString().last()) {
            '1' -> "${num}st"
            '2' -> "${num}nd"
            '3' -> "${num}rd"
            else -> "${num}th"
        }
    }
}