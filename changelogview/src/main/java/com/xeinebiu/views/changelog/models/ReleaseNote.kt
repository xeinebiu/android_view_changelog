package com.xeinebiu.views.changelog.models

/**
 * Represents a Release Note with its Change Logs
 * @author xeinebiu
 */
data class ReleaseNote(
    val title: String,
    val notes: List<String>
)
