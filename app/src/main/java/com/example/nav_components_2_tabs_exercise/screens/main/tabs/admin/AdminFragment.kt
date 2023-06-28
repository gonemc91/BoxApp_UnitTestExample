package com.example.nav_components_2_tabs_exercise.screens.main.tabs.admin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentAdminTreeBinding
import com.example.nav_components_2_tabs_exercise.utils.resources.ContextResources
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator

/**
 * Contains only RecycleView which displays they whole tree od data from the database
 * starting from accounts and ending with box settings.
 */



class AdminFragment : Fragment(R.layout.fragment_admin_tree) {



    private lateinit var binding: FragmentAdminTreeBinding

    private val viewModel by viewModelCreator {
        AdminViewModel(Repositories.accountsRepository, ContextResources(requireContext()))
    }

    private lateinit var adapter: AdminItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminTreeBinding.bind(view)

        val layoutManager = LinearLayoutManager(requireContext())
        adapter=AdminItemsAdapter(viewModel)


        binding.adminThreeRecycleView.layoutManager = layoutManager
        binding.adminThreeRecycleView.adapter = adapter

        observeTreeItems()
    }

    private fun observeTreeItems(){
        viewModel.items.observe(viewLifecycleOwner){treeItems->
            adapter.renderItems(treeItems)
        }
    }

}