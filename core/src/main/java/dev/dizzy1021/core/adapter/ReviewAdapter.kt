package dev.dizzy1021.core.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.dizzy1021.core.R
import dev.dizzy1021.core.adapter.event.OnItemClickCallback
import dev.dizzy1021.core.databinding.ItemListReviewBinding
import dev.dizzy1021.core.domain.model.Review

class ReviewAdapter : PagingDataAdapter<Review, ReviewAdapter.ListViewHolder>(DiffCallback) {

    private var onItemClickCallback: OnItemClickCallback<Review>? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback<Review>) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListReviewBinding.bind(itemView)

        fun bind(items: Review) {
            with(binding) {
                reviewPlace.text = items.placeName
                reviewDate.text = items.date
                reviewDesc.text = items.desc
                reviewUser.text = StringBuilder()
                    .append(items.rating).append(" • ").append(items.username)
            }

            itemView.setOnClickListener { onItemClickCallback?.onItemClicked(items) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ListViewHolder =
        ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_review, parent, false)
        )

    override fun onBindViewHolder(holder: ReviewAdapter.ListViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    object DiffCallback : DiffUtil.ItemCallback<Review>() {

        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return (oldItem.username == newItem.username) && (oldItem.placeId == newItem.placeId)
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
}