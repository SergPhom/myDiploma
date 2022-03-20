package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardProgressBinding

class PostLoadingStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<CardProgressViewHolder>() {
    override fun onBindViewHolder(holder: CardProgressViewHolder,
                                  loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): CardProgressViewHolder =
        CardProgressBinding.inflate(LayoutInflater.from(parent.context), parent,false)
            .let { CardProgressViewHolder(it, retry) }

}

class CardProgressViewHolder(
    private val binding: CardProgressBinding,
    private val retry: () -> Unit,
) :RecyclerView.ViewHolder(binding.root){
    fun bind(loadState: LoadState){
        binding.apply {
            progress.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState is LoadState.Error
            retryButton.setOnClickListener {
                retry()
            }
        }
    }
}