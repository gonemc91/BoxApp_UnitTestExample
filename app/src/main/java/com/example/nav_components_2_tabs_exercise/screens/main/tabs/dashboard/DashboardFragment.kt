package com.example.nav_components_2_tabs_exercise.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.FragmentDashboardBinding
import com.example.nav_components_2_tabs_exercise.model.boxes.entities.Box
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator

class DashboardFragment: Fragment(R.layout.fragment_dashboard) {
    private lateinit var binding: FragmentDashboardBinding

    private val viewModel by viewModelCreator { DashboardViewModel(Repositories.boxesRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)

        clearBoxViews()

        viewModel.boxes.observe(viewLifecycleOwner){renderBoxes(it)}

    }

    private fun renderBoxes(boxes: List<Box>){
        clearBoxViews()
        if(boxes.isEmpty()){
            binding.noBoxesTextView.visibility = View.VISIBLE
            binding.boxesContainer.visibility = View.VISIBLE
        } else{
            binding.noBoxesTextView.visibility = View.INVISIBLE
            binding.boxesContainer.visibility = View.VISIBLE
            createBoxes(boxes)
        }

    }

    private fun createBoxes(boxes: List<Box>){
        // let's create boxes here by using dynamic view generation


        val width = resources.getDimensionPixelSize(R.dimen.dashboard_item_width)
        val height = resources.getDimensionPixelSize(R.dimen.dashboard_item_height)
        val generatedIdentifiers = boxes.map {box->
        val id = View.generateViewId()


        }

    }


    private fun clearBoxViews(){
        binding.boxesContainer.removeViews(1, binding.root.childCount-1)
    }


}