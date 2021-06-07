package dev.dizzy1021.wander.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import dev.dizzy1021.core.adapter.PlaceAdapter
import dev.dizzy1021.core.adapter.PlaceLoadStateAdapter
import dev.dizzy1021.core.adapter.event.OnItemClickCallback
import dev.dizzy1021.core.domain.model.Place
import dev.dizzy1021.core.utils.SharedPreferenceUtil
import dev.dizzy1021.core.utils.isNetworkAvailable
import dev.dizzy1021.core.utils.withLoadStateAdapters
import dev.dizzy1021.wander.BuildConfig
import dev.dizzy1021.wander.R
import dev.dizzy1021.wander.databinding.FragmentSearchBinding
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding as FragmentSearchBinding

    private lateinit var adapter: PlaceAdapter

    private val viewModel: SearchViewModel by viewModels()
    private var picturePath: String? = null
    private var inputStream: InputStream? = null
    private var photoFile: File? = null

    private lateinit var fusedLocation: FusedLocationProviderClient

    @Inject
    lateinit var pref: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())

        getPosition()
    }

    @SuppressLint("MissingPermission")
    private fun getPosition() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocation.lastLocation
                    .addOnSuccessListener { loc: Location? ->
                        lifecycleScope.launch {
                            viewModel.queryChannel.send("geo:${loc?.latitude.toString()},${loc?.longitude.toString()}")
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please turn on" + " your location...",
                    Toast.LENGTH_LONG
                )
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 55
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()

        val actionBar = requireActivity().findViewById<Toolbar>(R.id.main_toolbar)
        actionBar.isGone = true
        actionBar.title = null

        if (isNetworkAvailable(requireActivity())) {

            val searchManager =
                requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

            binding.networkError.isGone = true
            binding.searchData.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

            binding.searchData.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return query != null
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    lifecycleScope.launch {
                        viewModel.queryChannel.send(newText.toString())
                    }
                    adapter.refresh()
                    return true
                }

            })

        } else {
            binding.networkError.isVisible = true
            binding.rvSearch.isGone = true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener { activity?.onBackPressed() }
        binding.captureImage.setOnClickListener { openCamera() }
        binding.uploadImage.setOnClickListener { openFileChooser() }

        adapter = PlaceAdapter()
        binding.rvSearch.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )

        binding.rvSearch.adapter = adapter.withLoadStateAdapters(
            header = PlaceLoadStateAdapter { adapter.refresh() },
            footer = PlaceLoadStateAdapter { adapter.retry() }
        )

        binding.rvSearch.setHasFixedSize(true)

        adapter.setOnItemClickCallback(object : OnItemClickCallback<Place> {
            override fun onItemClicked(data: Place) {
                navigateToDetailPlace(data.id)
            }
        })

        observeData()

        return binding.root
    }

    private fun observeData() {

        val user = pref.getUser()

        user?.let {
            lifecycleScope.launch {
                viewModel.places(it, inputStream).collectLatest { places ->
                    binding.networkError.isGone = true
                    binding.rvSearch.isVisible = true

                    adapter.submitData(places)
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {

        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        picturePath = image.absolutePath
        return image
    }

    private var resultCapture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                lifecycleScope.launch {
                    val compressed = photoFile?.let { compressImage(it) }
                    if (compressed != null) {
                        postImage(compressed)
                    }
                }
            }
        }

    private fun postImage(file: File) {
        inputStream = FileInputStream(file)
        observeData()
    }

    private var resultChooser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                inputStream = uri?.let { activity?.contentResolver?.openInputStream(it) };
                observeData()
            }
        }

    private suspend fun compressImage(photo: File) =
        Compressor.compress(
            requireContext(),
            photo,
            Dispatchers.Unconfined
        )

    private fun openCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (activity?.let { takePictureIntent.resolveActivity(it.packageManager) } != null) {
            try {
                photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(
                    Objects.requireNonNull(requireContext()),
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile!!
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultCapture.launch(takePictureIntent)

            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Error: ${ex.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Error: null", Toast.LENGTH_LONG).show()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        resultChooser.launch(chooser)
    }

    private fun navigateToDetailPlace(id: String) {
        val toDetail =
            SearchFragmentDirections.actionSearchFragmentToPlaceFragment(id)
        findNavController().navigate(toDetail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}