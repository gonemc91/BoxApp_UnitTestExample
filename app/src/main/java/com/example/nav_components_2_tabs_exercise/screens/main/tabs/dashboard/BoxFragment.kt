package com.example.nav_components_2_tabs_exercise.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentBoxBinding
import com.example.nav_components_2_tabs_exercise.utils.observeEvent
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator

class BoxFragment: Fragment(R.layout.fragment_box) {

    private lateinit var binding: FragmentBoxBinding

    private val viewModel by viewModelCreator { BoxViewModel(getBoxId(), Repositories.boxesRepository) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBoxBinding.bind(view)


        binding.root.setBackgroundColor(TODO("DashBoardItem"))
        binding.boxTextView.text = getString(R.string.this_is_box, getColorName())

        binding.goBackButton.setOnClickListener { omGoBackButtonPressed() }

        listenShouldExitEvent()

    }


    private fun omGoBackButtonPressed() {
        TODO("Go back to the screen here")
    }


    private fun listenShouldExitEvent() =
        viewModel.shouldExitEvent.observeEvent(viewLifecycleOwner) { shouldExit ->
            if (shouldExit) {
                //close the screen if the box has been deactivated
                TODO("Go back to the previous screen here")
            }

        }


    private fun getBoxId(): Int {
        TODO("Extract box id from arguments here")
    }

    private fun getColorValue(): Int {
        TODO("Extract color value from arguments here")
    }

    private fun getColorName(): String {
        TODO("Extract color name from arguments here")
    }




}