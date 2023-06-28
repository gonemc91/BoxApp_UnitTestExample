package com.example.nav_components_2_tabs_exercise.screens.main.tabs.admin


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.model.accounts.AccountsRepository
import com.example.nav_components_2_tabs_exercise.utils.resources.Resources
import com.example.nav_components_2_tabs_exercise.utils.share
import kotlinx.coroutines.flow.MutableStateFlow

class AdminViewModel(
    private val accountRepository: AccountsRepository,
    private val resources: Resources,
) : ViewModel(), AdminItemAdapter.Listener {

    private val _items = MutableLiveData<List<AdminTreeItem>>()
    val items = _items.share()

    //here we store IDs of items which are expended
    private val expandedIdentifiers = mutableSetOf(getRootId())
    //state flow is used for notifying about changes when some item expends/collapse
    private val expandedItemsStateFlow = MutableStateFlow(ExpansionsState(expandedIdentifiers))




    private fun getRootId(): Long = 1L

    override fun onItemToggled(item: AdminTreeItem) {
        TODO("Not yet implemented")
    }




    private fun settingToMap(isActive: Boolean): Map<String, String>{
        val isActiveString = resources.getString(if (isActive) R.string.yes else R.string.no)
        return mapOf(
            resources.getString(R.string.setting_is_active) to isActiveString
        )
    }




    private class Node(
        val id: Long,
        val attributes: Map<String, String>,
        val expansionStatus: ExpansionStatus,
        val nodes: List<Node>
    )

    private class ExpansionsState(
        val identifiers: Set<Long>
    )


    private companion object{
        const val ACCOUNT_MASK = 0x2L shl 60
        const val BOX_MASK = 0x3L shl 60
        const val SETTING_MASK = 0x4L shl 60

    }


}