package de.byjoker.myjfql.lang

import java.util.regex.Pattern

class StringComparator : Comparator<String> {

    override fun compare(o1: String, o2: String): Int {
        return when {
            o1 == "null" -> {
                if (o2 == "null") {
                    0
                } else 1
            }
            isNumber(o1) -> {
                if (o2 == "null") {
                    return -1
                }
                if (!isNumber(o2)) {
                    -1
                } else o1.toLong().compareTo(o2.toLong())
            }
            else -> {
                if (o2 == "null") {
                    return -1
                }
                if (isNumber(o2)) {
                    1
                } else o1.compareTo(o2)
            }
        }
    }

    private fun isNumber(s: String): Boolean {
        return Pattern.compile("\\d*").matcher(s).matches()
    }

}
