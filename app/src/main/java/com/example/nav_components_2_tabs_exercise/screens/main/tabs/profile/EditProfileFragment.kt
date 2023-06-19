package com.example.nav_components_2_tabs_exercise.screens.main.tabs.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentEditProfileBinding
import com.example.nav_components_2_tabs_exercise.utils.observeEvent
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var binding: FragmentEditProfileBinding

    private val viewModel by viewModelCreator { EditProfileViewModel(Repositories.accountsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        binding.saveButton.setOnClickListener { onSaveButtonPressed() }
        binding.cancelButton.setOnClickListener { onCancelButtonPressed() }

        if (savedInstanceState == null) listenInitialUsernameEvent()
        observeGoBackEvent()
        observeSaveInProgress()
        observeEmptyFieldErrorEvent()
    }

    private fun onSaveButtonPressed() {
        viewModel.saveUsername(binding.usernameTextInput.text.toString())
    }

    private fun observeSaveInProgress() = viewModel.saveInProgress.observe(viewLifecycleOwner) {
        if (it) {
            binding.progressBar.visibility = View.VISIBLE
            binding.saveButton.isEnabled = false
            binding.usernameTextInput.isEnabled = false
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.saveButton.isEnabled = true
            binding.usernameTextInput.isEnabled = true
        }
    }

    private fun listenInitialUsernameEvent() = viewModel.initialUsernameEvent.observeEvent(viewLifecycleOwner) { username ->
        binding.usernameTextInput.setText(username)
    }

    private fun observeEmptyFieldErrorEvent() = viewModel.showEmptyFieldErrorEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(requireContext(), R.string.field_is_empty, Toast.LENGTH_SHORT).show()
    }

    private fun onCancelButtonPressed() {
        findNavController().popBackStack()
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }
}