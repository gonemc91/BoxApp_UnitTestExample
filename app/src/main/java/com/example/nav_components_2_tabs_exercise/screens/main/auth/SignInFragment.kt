package com.example.nav_components_2_tabs_exercise.screens.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentSignInBinding
import com.example.nav_components_2_tabs_exercise.utils.observeEvent
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator

class SignInFragment: Fragment(R.layout.fragment_sign_in) {

    private lateinit var binding: FragmentSignInBinding

    private val viewModel by viewModelCreator {SignInViewModel(Repositories.accountRepository)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignInBinding.bind(view)
        binding.signInButton.setOnClickListener{ onSignInButtonPressed() }
        binding.signUpButton.setOnClickListener { onSignUpButtonPressed() }

        observeState()
        observeCleanPasswordEvent()
        observeShowAuthErrorMessageEvent()
        observeNavigateToTabsEvent()
    }

    private fun onSignInButtonPressed() {
      viewModel.signIn(
          email = binding.emailEditText.text.toString(),
          password = binding.passwordEditText.toString()
      )
    }


    private fun observeState() = viewModel.state.observe(viewLifecycleOwner){
        binding.emailTextInput.error = if(it.emptyEmailError) getString(R.string.field_is_empty) else null
        binding.passwordTextInput.error = if(it.emptyPasswordError) getString(R.string.field_is_empty) else null

        binding.emailTextInput.isEnabled = it.enableViews
        binding.passwordTextInput.isEnabled = it.enableViews
        binding.signInButton.isEnabled = it.enableViews
        binding.signUpButton.isEnabled = it.enableViews
        binding.progressBar.visibility = if(it.showProgress) View.VISIBLE else View.INVISIBLE
    }

    private  fun observeShowAuthErrorMessageEvent() = viewModel.showAuthToastEvent.observeEvent(viewLifecycleOwner){
        Toast.makeText(requireContext(), R.string.invalid_email_or_password, Toast.LENGTH_SHORT).show()
    }

    private fun observeCleanPasswordEvent() = viewModel.clearPasswordEvent.observeEvent(viewLifecycleOwner){
        binding.passwordEditText.text?.clear()
    }

    private fun observeNavigateToTabsEvent() = viewModel.navigateToTabEvent.observeEvent(viewLifecycleOwner){
        //user has signed successfully
        TODO("Replace SignInFragment by TabsFragment here")
    }



    private fun onSignUpButtonPressed() {
        val email = binding.emailEditText.text.toString()
        val emailArg = if (email.isBlank())
            null
        else{
            email
        }
        // user want to create a new account
        TODO("Launch SignUpFragment here and send emailArg to it")

    }


}