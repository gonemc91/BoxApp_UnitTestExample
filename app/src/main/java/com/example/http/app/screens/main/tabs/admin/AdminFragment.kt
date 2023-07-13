package com.example.http.app.screens.main.tabs.admin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.http.R
import com.example.http.databinding.FragmentAdminTreeBinding
import com.example.http.app.utils.resources.ContextResources
import com.example.http.app.utils.viewModelCreator

/**
 * Contains only RecycleView which displays they whole tree od data from the database
 * starting from accounts and ending with box settings.
 */



class AdminFragment : Fragment(R.layout.fragment_admin_tree) {



    private lateinit var binding: FragmentAdminTreeBinding

    private val viewModel by viewModelCreator {
        AdminViewModel(Repositories.accountsSources, ContextResources(requireContext()))
    }

    private lateinit var adapter: AdminItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminTreeBinding.bind(view)

        val layoutManager = LinearLayoutManager(requireContext())
        adapter= AdminItemsAdapter(viewModel)


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