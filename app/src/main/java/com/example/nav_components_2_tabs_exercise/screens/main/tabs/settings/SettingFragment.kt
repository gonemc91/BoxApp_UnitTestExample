package com.example.nav_components_2_tabs_exercise.screens.main.tabs.settings

import android.os.Bundle
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentSettingsBinding
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator

class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel by viewModelCreator { SettingsViewModel(Repositories.boxesRepository) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        val adapter = setupList()
        viewModel.boxSettings.observe(viewLifecycleOwner){
            adapter.renderSettings(it)
        }
    }

    private fun setupList(): SettingAdapter{
        binding.settingsList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SettingAdapter(viewModel)
        binding.settingsList.adapter = adapter
        return adapter
    }

}