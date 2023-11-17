package com.example.http.presentation.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.http.domain.accounts.entities.SignUpData
import com.example.http.presentation.base.BaseFragment
import com.example.http.utils.observeEvent
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.databinding.FragmentSignUpBinding
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment: BaseFragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding

    override val viewModel by viewModels<SignUpViewModel>()

    private val args by navArgs<SignUpFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpBinding.bind(view)
        binding.createAccountButton.setOnClickListener { onCreateAccountButtonPressed() }

        if (savedInstanceState == null && getEmailArgument() != null){
            binding.emailEditText.setText(getEmailArgument())
        }

        observeState()
        observeGoBackEvent()
        observeShowSuccessSignUpMessageEvent()
    }

    private fun onCreateAccountButtonPressed() {
        val signUpData = SignUpData(
            email = binding.emailEditText.text.toString(),
            username = binding.usernameEditText.text.toString(),
            password = binding.passwordEditText.text.toString(),
            repeatPassword = binding.repeatPasswordEditText.text.toString()
        )
        viewModel.signUp(signUpData)

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

        binding.progressBar.visibility = if(state.showProgress) View.VISIBLE else View.INVISIBLE

    }

    private fun observeShowSuccessSignUpMessageEvent() = viewModel.showToastEvent.observeEvent(viewLifecycleOwner){
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
        findNavController().popBackStack()
    }

    private fun getEmailArgument(): String? =  args.emailArg


}