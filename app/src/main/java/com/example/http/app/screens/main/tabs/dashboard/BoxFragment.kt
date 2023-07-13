package com.example.http.app.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.http.R
import com.example.http.databinding.FragmentBoxBinding
import com.example.http.app.utils.observeEvent
import com.example.http.app.utils.viewModelCreator
import com.example.http.app.views.DashboardItemView

class BoxFragment: Fragment(R.layout.fragment_box) {

    private lateinit var binding: FragmentBoxBinding

    private val viewModel by viewModelCreator { BoxViewModel(getBoxId(), Repositories.boxesRepository) }

    private val args by navArgs<BoxFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBoxBinding.bind(view)


        binding.root.setBackgroundColor(DashboardItemView.getBackgroundColor(getColorValue()))
        binding.boxTextView.text = getString(R.string.this_is_box, getColorName())

        binding.goBackButton.setOnClickListener { omGoBackButtonPressed() }

        listenShouldExitEvent()

    }


    private fun omGoBackButtonPressed() {
        findNavController().popBackStack()
    }


    private fun listenShouldExitEvent() =
        viewModel.shouldExitEvent.observeEvent(viewLifecycleOwner) { shouldExit ->
            if (shouldExit) {
                //close the screen if the box has been deactivated
                findNavController().popBackStack()
            }
        }


    private fun getBoxId(): Long = args.boxID

    private fun getColorValue(): Int = args.boxColorValue

    private fun getColorName(): String = args.boxName




}