package dev.dizzy1021.wander.ui.wishlist

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.dizzy1021.core.adapter.PlaceAdapter
import dev.dizzy1021.core.adapter.PlaceLoadStateAdapter
import dev.dizzy1021.core.adapter.event.OnItemClickCallback
import dev.dizzy1021.core.domain.model.Place
import dev.dizzy1021.core.utils.SharedPreferenceUtil
import dev.dizzy1021.core.utils.isNetworkAvailable
import dev.dizzy1021.core.utils.withLoadStateAdapters
import dev.dizzy1021.wander.R
import dev.dizzy1021.wander.databinding.FragmentWishlistBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding as FragmentWishlistBinding
    private val viewModel: WishlistViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val actionBar = requireActivity().findViewById<Toolbar>(R.id.main_toolbar)
        actionBar.title = "Wishlist"
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
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PlaceAdapter()

        binding.rvWishlist.adapter = adapter.withLoadStateAdapters(
            header = PlaceLoadStateAdapter { adapter.refresh() },
            footer = PlaceLoadStateAdapter { adapter.retry() }
        )

        binding.rvWishlist.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.rvWishlist.setHasFixedSize(true)

        adapter.setOnItemClickCallback(object : OnItemClickCallback<Place> {
            override fun onItemClicked(data: Place) {
                navigateToDetailPlace(data.id)
            }
        })

        val user = pref.getUser()

        if (isNetworkAvailable(requireActivity())) {
            user?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.wishlist(it).collectLatest { places ->
                        binding.rvWishlist.isVisible = true
                        adapter.submitData(places)
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    adapter.loadStateFlow.collectLatest { loadStates ->
                        binding.shimmerContainer.isVisible = loadStates.refresh is LoadState.Loading
                        binding.networkError.isVisible = loadStates.refresh is LoadState.Error
                        if (loadStates.append is LoadState.NotLoading && loadStates.append.endOfPaginationReached) {
                            binding.noData.isVisible = adapter.itemCount < 1
                        }
                    }
                }
            }

        } else {
            binding.networkError.isVisible = true
            binding.rvWishlist.isGone = true
        }
    }

    private fun navigateToDetailPlace(id: String) {
        val toDetail =
            WishlistFragmentDirections.actionWishlistFragmentToPlaceFragment(id)
        findNavController().navigate(toDetail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}