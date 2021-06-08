package dev.dizzy1021.wander.ui.home

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
import dev.dizzy1021.wander.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val actionBar = requireActivity().findViewById<Toolbar>(R.id.main_toolbar)
        actionBar.title = "Home"
        actionBar.isVisible = true

        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                return true
            }
            R.id.wishlist -> {
                findNavController().navigate(R.id.action_homeFragment_to_wishlistFragment)
                return true
            }
            R.id.review -> {
                findNavController().navigate(R.id.action_homeFragment_to_reviewFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PlaceAdapter()

        binding.rvHome.adapter = adapter.withLoadStateAdapters(
            header = PlaceLoadStateAdapter { adapter.refresh() },
            footer = PlaceLoadStateAdapter { adapter.retry() }
        )

        binding.rvHome.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.rvHome.setHasFixedSize(true)

        adapter.setOnItemClickCallback(object : OnItemClickCallback<Place> {
            override fun onItemClicked(data: Place) {
                navigateToDetailPlace(data.id)
            }
        })

        val user = pref.getUser()

        if (isNetworkAvailable(requireActivity())) {
            user?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.places(it).collectLatest { places ->
                        binding.rvHome.isVisible = true
                        adapter.submitData(places)

                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    adapter.loadStateFlow.collectLatest { loadStates ->
                        binding.shimmerContainer.isVisible = loadStates.refresh is LoadState.Loading
                        binding.networkError.isVisible = loadStates.refresh is LoadState.Error
                    }
                }
            }

        } else {
            binding.networkError.isVisible = true
            binding.rvHome.isGone = true
        }
    }

    private fun navigateToDetailPlace(id: String) {
        val toDetail =
            HomeFragmentDirections.actionHomeFragmentToPlaceFragment(id)
        findNavController().navigate(toDetail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}