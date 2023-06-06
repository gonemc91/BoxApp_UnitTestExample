package com.example.nav_components_2_tabs_exercise.screens.main.tabs.profile


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentProfileBinding
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.utils.observeEvent
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator
import java.text.SimpleDateFormat

class ProfileFragment: Fragment(R.layout.fragment_profile) {

    lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModelCreator { ProfileViewModel(Repositories.accountRepository)}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)


        binding.editProfileButton.setOnClickListener { onEditProfileButtonPressed() }
        binding.logoutButton.setOnClickListener { onLogoutButtonPressed() }


        observeAccountDetails()
        observeRestartAppFromLoginScreenEvent()

    }

    private fun observeAccountDetails(){
        val formatter = SimpleDateFormat.getDateTimeInstance()
        viewModel.account.observe(viewLifecycleOwner){account->
            if (account == null) return@observe
            binding.emailTextView.text = account.email
            binding.usernameTextView.text = account.userName
            binding.createdAtTextView.text = if(account.createdAt == Account.UNKNOWN_CREATED_AT)
                getString(R.string.placeholder)
            else
                formatter.format(account.createdAt)
        }


    }

    private fun onEditProfileButtonPressed() {
        TODO("Launch EditProfileFragment gere over tabs (tabs should be available from EditProfileFragment")
    }

    private fun observeRestartAppFromLoginScreenEvent(){
        viewModel.restartWithSignInEvent.observeEvent(viewLifecycleOwner){
            //user has signed out from the app
            TODO("Close all tab screens and launch SignInFragment here")
        }
    }


    private fun onLogoutButtonPressed() {
        viewModel.logout()
    }

}