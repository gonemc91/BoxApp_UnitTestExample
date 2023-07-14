package com.example.http.app.screens.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.example.http.app.utils.findTopNavController
import com.example.http.app.utils.observeEvent
import com.example.nav_components_2_tabs_exercise.R

abstract class BaseFragment(@LayoutRes layoutId: Int): Fragment(layoutId) {

    abstract val viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showErrorMessageEvent.observeEvent(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }


        viewModel.showErrorMessageResEvent.observeEvent(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }


        viewModel.showAuthErrorAndRestartEvent.observeEvent(viewLifecycleOwner){
            Toast.makeText(requireContext(), getString(R.string.auth_error), Toast.LENGTH_SHORT).show()
            logout()
        }

    }

    fun logout(){
        viewModel.logout()
        restartWithSignIn()
    }


    private fun restartWithSignIn(){
        findTopNavController().navigate(R.id.signInFragment, null, navOptions {
            popUpTo(R.id.tabsFragment){
                inclusive = true
            }
        })
    }


}