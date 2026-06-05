package com.example.mqttclient.ui.util

import java.text.DateFormat
import java.util.Date

/**
 * Formats an epoch-millis timestamp as a locale-aware date/time string.
 * Replaces the deprecated `Date.toLocaleString()`.
 */
fun formatTimestamp(epochMillis: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
        .format(Date(epochMillis))
