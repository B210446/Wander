package dev.dizzy1021.wander.ui.place

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
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


    private lateinit var latitude: String
    private lateinit var longitude: String

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
            user?.let {
                viewModel.places(idPlace, it).observe(viewLifecycleOwner, { place ->
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
                                        this.placeDesc.text = it?.desc
                                        this.placeLocation.text = it?.location
                                        this.placeName.text = it?.name
                                        this.placeRating.text = it?.rating.toString()

                                        pagerAdapter = GalleryPagerAdapter(
                                            this@PlaceFragment,
                                            it?.gallery
                                        )

                                        binding.pagerGallery.adapter = pagerAdapter

                                        latitude = it?.latitude.toString()
                                        longitude = it?.longitude.toString()

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
            val gmmIntentUri: Uri = Uri.parse("geo:$latitude,$longitude")
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}