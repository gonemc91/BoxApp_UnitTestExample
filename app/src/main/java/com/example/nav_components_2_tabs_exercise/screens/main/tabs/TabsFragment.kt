package com.example.nav_components_2_tabs_exercise.screens.main.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.databinding.FragmentTabsBinding

class TabsFragment : Fragment(R.layout.fragment_tabs) {

    private lateinit var binding: FragmentTabsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTabsBinding.bind(view)

        TODO("Connect Nav Components to the BottomNavigationView")

    }

}