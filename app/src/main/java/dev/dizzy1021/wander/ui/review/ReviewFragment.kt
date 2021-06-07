package dev.dizzy1021.wander.ui.review

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.dizzy1021.core.adapter.PlaceLoadStateAdapter
import dev.dizzy1021.core.adapter.ReviewAdapter
import dev.dizzy1021.core.adapter.event.OnItemClickCallback
import dev.dizzy1021.core.domain.model.Review
import dev.dizzy1021.core.utils.SharedPreferenceUtil
import dev.dizzy1021.core.utils.isNetworkAvailable
import dev.dizzy1021.core.utils.withLoadStateAdapters
import dev.dizzy1021.wander.R
import dev.dizzy1021.wander.databinding.FragmentReviewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding as FragmentReviewBinding
    private val viewModel: ReviewViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val actionBar = requireActivity().findViewById<Toolbar>(R.id.main_toolbar)
        actionBar.title = "My Review"
        actionBar.isVisible = true

        actionBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        actionBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReviewAdapter()

        binding.rvReview.adapter = adapter.withLoadStateAdapters(
            header = PlaceLoadStateAdapter { adapter.refresh() },
            footer = PlaceLoadStateAdapter { adapter.retry() }
        )

        binding.rvReview.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.rvReview.setHasFixedSize(true)

        adapter.setOnItemClickCallback(object : OnItemClickCallback<Review> {
            override fun onItemClicked(data: Review) {
                navigateToDetailReview(data.id)
            }
        })
        val user = pref.getUser()

        if (isNetworkAvailable(requireActivity())) {
            user?.let {
                lifecycleScope.launch {
                    viewModel.review(it).collectLatest { places ->
                        binding.networkError.isGone = true
                        binding.rvReview.isVisible = true
                        adapter.submitData(places)
                    }
                }
            }

        } else {
            binding.networkError.isVisible = true
            binding.rvReview.isGone = true
        }
    }

    private fun navigateToDetailReview(id: String) {
        val toDetail = ReviewFragmentDirections.actionReviewFragmentToPlaceFragment(id)
        findNavController().navigate(toDetail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}