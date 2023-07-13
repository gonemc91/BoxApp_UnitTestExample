package com.example.http.app.screens.main.tabs.admin

enum class ExpansionStatus{
    EXPANDED,
    COLLAPSED,
    NOT_EXPANDABLE
}

data class AdminTreeItem(
    val id: Long,
    val level: Int,
    val expansionStatus: ExpansionStatus,
    val attribute: Map<String, String>
)