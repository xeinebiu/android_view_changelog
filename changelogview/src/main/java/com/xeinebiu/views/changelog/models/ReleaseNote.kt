package com.xeinebiu.views.changelog.models

/**
 * Represents a Release Note with its Change Logs
 * @author xeinebiu
 */
data class ReleaseNote constructor(
    val title: String,
    val notes: List<String>
)
