package com.example.nav_components_2_tabs_exercise.screens.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentSignUpBinding
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.SignUpData
import com.example.nav_components_2_tabs_exercise.utils.observeEvent
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment: Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel by viewModelCreator {  SignUpViewModel(Repositories.accountRepository)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpBinding.bind(view)
        binding.createAccountButton.setOnClickListener { onCreateAccountButtonPressed() }

    }

    private fun onCreateAccountButtonPressed() {
        val signUpData = SignUpData(
            email = binding.emailEditText.text.toString(),
            username = binding.usernameEditText.text.toString(),
            password = binding.passwordEditText.text.toString(),
            repeatPassword = binding.repeatPasswordEditText.text.toString()
        )
        viewModel.signUp(signUpData)

        observeState()
        observeGoBackEvent()
        observeShowSuccessSignUpMessageEvent()
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner){state->
        binding.createAccountButton.isEnabled = state.enableViews
        binding.emailTextInput.isEnabled = state.enableViews
        binding.usernameTextInput.isEnabled = state.enableViews
        binding.usernameTextInput.isEnabled = state.enableViews
        binding.repeatPasswordTextInput.isEnabled = state.enableViews


        fillError(binding.emailTextInput, state.emailErrorMessageRes)
        fillError(binding.usernameTextInput, state.usernameErrorMessageRes)
        fillError(binding.passwordTextInput, state.passwordErrorMessageRes)
        fillError(binding.repeatPasswordTextInput, state.repeatPasswordErrorMessageRes)

    }

    private fun observeShowSuccessSignUpMessageEvent() = viewModel.showSuccessSignUpMessageEvent.observeEvent(viewLifecycleOwner){
        Toast.makeText(requireContext(), R.string.sign_up_success, Toast.LENGTH_SHORT).show()
    }

    private fun fillError(input: TextInputLayout, @StringRes stringRes: Int){
        if(stringRes == SignUpViewModel.NO_ERROR_MESSAGE){
            input.error = null
            input.isErrorEnabled = false
        }else{
            input.error = getString(stringRes)
            input.isErrorEnabled = true
        }
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner){
        TODO("Go back to the previous screen here")
    }

    private fun getEmailArgument(): String{
        TODO("Extract email value from arguments here")
    }


}