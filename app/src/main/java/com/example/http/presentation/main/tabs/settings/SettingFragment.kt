package com.example.http.presentation.main.tabs.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.http.presentation.base.BaseFragment
import com.example.http.utils.observeResults
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    override val viewModel by viewModels<SettingsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)


        binding.resultView.setTryAgainAction { viewModel.tryAgain() }
        val adapter = setupList()
        viewModel.boxSettings.observeResults(this, view, binding.resultView) {
            adapter.renderSettings(it)
        }
    }

    private fun setupList(): SettingsAdapter {
        binding.settingsList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SettingsAdapter(viewModel)
        binding.settingsList.adapter = adapter
        return adapter
    }

}