package com.example.http.app.screens.main.tabs.dashboard

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.http.app.model.boxes.entities.Box
import com.example.http.app.screens.base.BaseFragment
import com.example.http.app.utils.observeResults
import com.example.http.app.views.DashboardItemView
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    override val viewModel by viewModels<DashboardViewModel>()

    private lateinit var binding: FragmentDashboardBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)

        clearBoxViews()

        binding.resultView.setTryAgainAction { viewModel.reload() }
        viewModel.boxes.observeResults(this, view, binding.resultView) {
            renderBoxes(it)
        }
    }

    private fun renderBoxes(boxes: List<Box>) {
        clearBoxViews()
        if (boxes.isEmpty()) {
            binding.noBoxesTextView.visibility = View.VISIBLE
            binding.boxesContainer.visibility = View.INVISIBLE
        } else {
            binding.noBoxesTextView.visibility = View.INVISIBLE
            binding.boxesContainer.visibility = View.VISIBLE
            createBoxes(boxes)
        }
    }

    private fun createBoxes(boxes: List<Box>) {

        // let's create boxes here by using dynamic view generation

        val width = resources.getDimensionPixelSize(R.dimen.dashboard_item_width)
        val height = resources.getDimensionPixelSize(R.dimen.dashboard_item_height)
        val generatedIdentifiers = boxes.map { box ->
            val id = View.generateViewId()
            val dashboardItemView = DashboardItemView(requireContext())
            dashboardItemView.setBox(box)
            dashboardItemView.id = id
            dashboardItemView.tag = box
            dashboardItemView.setOnClickListener(boxClickListener)
            val params = ConstraintLayout.LayoutParams(width, height)
            binding.boxesContainer.addView(dashboardItemView, params)
            return@map id
        }.toIntArray()
        binding.flowView.referencedIds = generatedIdentifiers
    }

    private fun clearBoxViews() {
        if (binding.boxesContainer.childCount > 1) {
            binding.boxesContainer.removeViews(1, binding.boxesContainer.childCount - 1)
        }
    }

    private val boxClickListener = View.OnClickListener {
        val box = it.tag as Box
        val direction = DashboardFragmentDirections.actionDashboardFragmentToBoxFragment(
            box.id,
            box.colorName,
            box.colorValue
        )
        findNavController().navigate(direction)
    }

}