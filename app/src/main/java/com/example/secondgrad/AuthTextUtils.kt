package com.example.secondgrad

fun trimInputText(value: String): String {
    var start = 0
    var end = value.length

    while (start < end) {
        val char = value[start]
        if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
            break
        }
        start = start + 1
    }

    while (end > start) {
        val char = value[end - 1]
        if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
            break
        }
        end = end - 1
    }

    return if (start >= end) "" else substringText(value, start, end)
}

fun substringText(value: String, start: Int, end: Int): String {
    if (start < 0 || end < start || start >= value.length) {
        return ""
    }

    val safeEnd = if (end > value.length) value.length else end
    val builder = StringBuilder()
    var index = start
    while (index < safeEnd) {
        builder.append(value[index])
        index = index + 1
    }
    return builder.toString()
}

fun indexOfChar(value: String, target: Char): Int {
    var index = 0
    while (index < value.length) {
        if (value[index] == target) {
            return index
        }
        index = index + 1
    }
    return -1
}

fun indexOfText(value: String, search: String): Int {
    return indexOfTextFrom(value, search, 0)
}

fun indexOfTextFrom(value: String, search: String, fromIndex: Int): Int {
    if (search.length == 0 || value.length < search.length || fromIndex < 0) {
        return -1
    }

    var start = fromIndex
    if (start > value.length - search.length) {
        return -1
    }

    while (start <= value.length - search.length) {
        var offset = 0
        var matched = true

        while (offset < search.length) {
            if (value[start + offset] != search[offset]) {
                matched = false
                break
            }
            offset = offset + 1
        }

        if (matched) {
            return start
        }
        start = start + 1
    }

    return -1
}

fun containsText(value: String, search: String): Boolean {
    return indexOfText(value, search) >= 0
}

fun toLowerCaseAscii(value: String): String {
    val builder = StringBuilder()
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (char >= 'A' && char <= 'Z') {
            builder.append((char.code + 32).toChar())
        } else {
            builder.append(char)
        }
        index = index + 1
    }
    return builder.toString()
}

fun hasInputText(value: String): Boolean {
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
            return true
        }
        index = index + 1
    }
    return false
}

fun charAtEnd(value: String): Char? {
    if (value.length == 0) {
        return null
    }
    return value[value.length - 1]
}
