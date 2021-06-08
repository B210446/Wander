package dev.dizzy1021.wander.ui.place

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import dev.dizzy1021.core.adapter.TopReviewAdapter
import dev.dizzy1021.core.utils.ResourceState
import dev.dizzy1021.core.utils.SharedPreferenceUtil
import dev.dizzy1021.core.utils.isNetworkAvailable
import dev.dizzy1021.wander.R
import dev.dizzy1021.wander.databinding.FragmentPlaceBinding
import dev.dizzy1021.wander.ui.place.adapter.GalleryPagerAdapter
import javax.inject.Inject


@AndroidEntryPoint
class PlaceFragment : Fragment() {

    private var _binding: FragmentPlaceBinding? = null
    private val binding get() = _binding as FragmentPlaceBinding
    private val viewModel: PlaceViewModel by viewModels()
    private lateinit var pagerAdapter: GalleryPagerAdapter
    private lateinit var reviewAdapter: TopReviewAdapter

    private var favorited: Boolean = false

    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var website: String

    @Inject
    lateinit var pref: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()

        val actionBar = requireActivity().findViewById<Toolbar>(R.id.main_toolbar)
        actionBar.title = null
        actionBar.isVisible = true

        actionBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        actionBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        inflater.inflate(R.menu.detail_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idPlace = PlaceFragmentArgs.fromBundle(arguments as Bundle).id
        val user = pref.getUser()
        val actionBar = requireActivity().findViewById<Toolbar>(R.id.main_toolbar)

        reviewAdapter = TopReviewAdapter()
        binding.rvTopReview.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.rvTopReview.adapter = reviewAdapter
        binding.rvTopReview.setHasFixedSize(true)


        if (isNetworkAvailable(requireActivity())) {
            user?.let { user_id ->
                viewModel.places(idPlace, user_id).observe(viewLifecycleOwner, { place ->
                    if (place != null) {
                        when (place.state) {
                            ResourceState.PENDING -> {

                                binding.apply {
                                    shimmerContainer.startShimmer()
                                    shimmerContainer.isVisible = true
                                    networkError.isGone = true
                                    mainView.isGone = true
                                    buttonGiveReview.isGone = true
                                }

                            }
                            ResourceState.SUCCESS -> {

                                binding.apply {
                                    shimmerContainer.stopShimmer()
                                    shimmerContainer.isGone = true
                                    networkError.isGone = true
                                    mainView.isVisible = true
                                    buttonGiveReview.isVisible = true
                                }

                                place.data.let {

                                    with(binding) {

                                        favorited = it?.isFavorite == true
                                        
                                        if (it?.isFavorite == true) {
                                            actionBar.menu.findItem(R.id.add_favorite).icon =
                                                ContextCompat.getDrawable(
                                                    requireContext(),
                                                    R.drawable.ic_baseline_favorite_24
                                                )
                                        } else {
                                            actionBar.menu.findItem(R.id.add_favorite).icon =
                                                ContextCompat.getDrawable(
                                                    requireContext(),
                                                    R.drawable.ic_baseline_favorite_border_24
                                                )
                                        }

                                        placeDesc.text = it?.desc
                                        placeLocation.text = it?.location
                                        placeName.text = it?.name
                                        placeRating.text = it?.rating.toString()

                                        buttonGiveReview.isGone = it?.isReviewed == true

                                        pagerAdapter = GalleryPagerAdapter(
                                            this@PlaceFragment,
                                            it?.gallery
                                        )

                                        binding.pagerGallery.adapter = pagerAdapter

                                        latitude = it?.latitude.toString()
                                        longitude = it?.longitude.toString()
                                        website = it?.website.toString()

                                        binding.indicatorGallery.text =
                                            "1 / ${pagerAdapter.itemCount}"



                                        it?.topReviews?.let { it1 -> reviewAdapter.submitList(it1) }

                                    }

                                }

                            }
                            ResourceState.FAILURE -> {

                                binding.apply {
                                    shimmerContainer.stopShimmer()
                                    shimmerContainer.isGone = true
                                    networkError.isVisible = true
                                    mainView.isGone = true
                                    buttonGiveReview.isGone = true
                                }

                            }
                        }
                    }
                })
            }
        } else {
            binding.shimmerContainer.stopShimmer()
            binding.shimmerContainer.isGone = true
            binding.networkError.isVisible = true
            binding.mainView.isGone = true
            binding.buttonGiveReview.isGone = true
        }

        binding.pagerGallery.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.indicatorGallery.text =
                    StringBuilder().append(position + 1).append(" / ")
                        .append(pagerAdapter.itemCount)
            }
        })

        binding.placeOpenMap.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.streetview:cbll=$longitude,$latitude")

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.buttonGiveReview.setOnClickListener {
            navigateToAddReview(idPlace)
        }

        binding.buttonAllReview.setOnClickListener {
            navigateToReview(idPlace)
        }



    }

    private fun navigateToReview(id: String) {
        val toReview =
            PlaceFragmentDirections.actionPlaceFragmentToGoogleReviewFragment(id)
        findNavController().navigate(toReview)
    }

    private fun navigateToAddReview(id: String) {
        val toAddReview =
            PlaceFragmentDirections.actionPlaceFragmentToCreateFeedbackFragment(id)
        findNavController().navigate(toAddReview)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_browser -> {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(website)
                startActivity(intent)
                return true
            }
            R.id.add_favorite -> {
                favorited = !favorited

                val user = pref.getUser()
                val idPlace = PlaceFragmentArgs.fromBundle(arguments as Bundle).id

                user?.let { viewModel.addFavorite(idPlace, it).observe(viewLifecycleOwner, { res ->
                    when (res.state) {
                        ResourceState.PENDING -> {}
                        ResourceState.SUCCESS -> {
                            if (favorited) {
                                item.setIcon(R.drawable.ic_baseline_favorite_24)
                            } else {
                                item.setIcon(R.drawable.ic_baseline_favorite_border_24)
                            }
                        }
                        ResourceState.FAILURE -> {}
                    }
                }) }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}