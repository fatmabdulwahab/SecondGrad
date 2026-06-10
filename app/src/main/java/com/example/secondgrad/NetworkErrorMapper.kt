package com.example.secondgrad

fun mapNetworkErrorMessage(throwable: Throwable, fallback: String): String {
    val message = throwable.message ?: ""
    val lower = toLowerCaseAscii(message)

    if (
        containsText(lower, "unable to resolve host") ||
        containsText(lower, "unknownhost") ||
        containsText(lower, "no address associated with hostname")
    ) {
        return "مش قادرين نوصل للسيرفر. تأكدي من الإنترنت وجربي شبكة تانية أو DNS تاني."
    }

    if (
        containsText(lower, "failed to connect") ||
        containsText(lower, "connection refused") ||
        containsText(lower, "network is unreachable")
    ) {
        return "مفيش اتصال بالسيرفر دلوقتي. اتأكدي من النت وجربي تاني."
    }

    if (containsText(lower, "timeout") || containsText(lower, "timed out")) {
        return "السيرفر أخد وقت طويل في الرد. جرّبي تاني."
    }

    if (containsText(lower, "cleartext") || containsText(lower, "not permitted")) {
        return "رابط السيرفر لازم يكون https مش http."
    }

    return if (message.length > 0) message else fallback
}
