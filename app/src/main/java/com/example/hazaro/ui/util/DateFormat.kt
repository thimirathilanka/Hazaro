package com.example.hazaro.ui.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatReportTime(millis: Long): String {
    if (millis <= 0L) return "Just now"
    val formatter = DateTimeFormatter
        .ofPattern("d MMM yyyy, h:mm a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(millis))
}
