package dev.dizzy1021.wander.ui.place.feedback

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.dizzy1021.wander.R
import dev.dizzy1021.wander.databinding.FragmentGoogleReviewBinding


@AndroidEntryPoint
class GoogleReviewFragment : Fragment() {

    private var _binding: FragmentGoogleReviewBinding? = null
    private val binding get() = _binding as FragmentGoogleReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()

        val actionBar = requireActivity().findViewById<Toolbar>(R.id.main_toolbar)
        actionBar.title = "Reviews"
        actionBar.isVisible = true

        actionBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        actionBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleReviewBinding.inflate(inflater, container, false)

        val idPlace = GoogleReviewFragmentArgs.fromBundle(arguments as Bundle).id
        val url = "https://search.google.com/local/reviews?placeid=ChIJaac90jeuEmsR0Evy-Wh9AQ8"

        binding.webViewReviews.settings.javaScriptEnabled = true
        binding.webViewReviews.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                val css = """.fYOrjf{display:none!important}.kp-header{display:none!important}.SiPMpc{display:none!important}.PQbOE{display:none!important}.EYn7Pc{display:none!important}.u1M3kd.g6Ealc{display:none!important}.tg6pY{display:none!important}localreviews-place-topics{display:none!important}.ERM0Ve{display:none!important}eRxGS{display:none!important}.TjbMZd:not(.ZVMCBd){display:none!important}.ERM0Ve{display:none!important}.tuv-lightbox-top-right-container{display:none!important}.tuv-thumbs-button-container-mobile .tuv-thumbs-up-button{display:none!important}.tuv-rap-button{display:none!important}.tuv-bottom-bar{display:none!important}"""

                view?.evaluateJavascript("(function() { " +
                        "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);" +
                        "var anchors = document.getElementsByTagName(\"a\");" +
                        "for (var i = 0; i < anchors.length; i++) {" +
                        "anchors[i].removeAttribute(\"href\");" +
                        "}})(); ", null)
            }
        }
        
        binding.webViewReviews.loadUrl(url)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}