package com.example.nav_components_2_tabs_exercise.screens.main.tabs.admin

enum class ExpansionStatus{
    EXPANDED,
    COLLAPSED,
    NOT_EXPANDED
}

data class AdminTreeItem(
    val id: Long,
    val level: Int,
    val expansionStatus: ExpansionStatus,
    val attribute: Map<String, String>
)