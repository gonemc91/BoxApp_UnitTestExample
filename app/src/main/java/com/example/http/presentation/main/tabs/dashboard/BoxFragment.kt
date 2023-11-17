package com.example.http.presentation.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.http.presentation.base.BaseFragment
import com.example.http.utils.observeEvent
import com.example.http.utils.viewModelCreator
import com.example.http.views.DashboardItemView
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.databinding.FragmentBoxBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BoxFragment: BaseFragment(R.layout.fragment_box) {

    @Inject lateinit var factory: BoxViewModel.Factory //inject factory for create parameter boxId for ViewModel

    private lateinit var binding: FragmentBoxBinding

    override val viewModel by viewModelCreator {
        factory.create(args.boxID)
    }

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