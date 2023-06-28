package com.example.nav_components_2_tabs_exercise.screens.main.tabs.admin

import androidx.recyclerview.widget.DiffUtil

class AdminTreeDiffCallback(
    private val oldList: List<AdminTreeItem>,
    private val newList: List<AdminTreeItem>
    ): DiffUtil.Callback()
  {
      override fun getOldListSize(): Int = oldList.size

      override fun getNewListSize(): Int = newList.size

      override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          return oldList[oldItemPosition].id == newList[newItemPosition].id
      }

      override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          return oldList[oldItemPosition] == newList[newItemPosition]
      }

  }