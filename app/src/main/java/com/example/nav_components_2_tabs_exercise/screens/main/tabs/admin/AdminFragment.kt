package com.example.nav_components_2_tabs_exercise.screens.main.tabs.admin

import androidx.fragment.app.Fragment
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

    private val ViewModel by viewModelCreator {
        AdminViewModel(Repositories.accountsRepository, ContextResources(requireContext()))
    }

}