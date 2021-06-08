package dev.dizzy1021.core.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.dizzy1021.core.R
import dev.dizzy1021.core.databinding.LayoutPlaceNetworkStateBinding

class PlaceLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PlaceLoadStateAdapter.LoadStateViewHolder>()
{

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) {
       holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder = LoadStateViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_place_network_state, parent, false)
    )

    inner class LoadStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = LayoutPlaceNetworkStateBinding.bind(view)

        fun bind(loadState: LoadState) {
            binding.btnRetry.isVisible = loadState !is LoadState.Loading
            binding.stateError.isVisible = loadState !is LoadState.Loading
//            binding.shimmerContainer.isVisible = loadState is LoadState.Loading

            if (loadState is LoadState.Error){
                binding.stateError.text = loadState.error.localizedMessage
            }

            binding.btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

    }
}