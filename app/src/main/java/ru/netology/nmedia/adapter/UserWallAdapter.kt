package ru.netology.nmedia.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


interface UserWallCallback {
    fun onLiked(post: Post){}
    fun onShared(post: Post){}
    fun onRemove(post: Post){}
    fun onEdit(post: Post){}
    fun onPlay(post: Post){}
    fun onSingleView(post: Post){}
    fun onSingleViewImageOnly(post: Post){}
    fun onSavingRetry(post: Post){}
    fun onUserProfile(author: String, authorId: Long){}
}
class UserWallAdapter(
    private val callback: Callback
): PagingDataAdapter<Post, RecyclerView.ViewHolder>(diffCallback) {

    companion object{
        val diffCallback = object : DiffUtil.ItemCallback<Post>(){
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                if (oldItem::class != newItem::class){
                    return false
                }
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = getItem(position)
        if(post != null){
            (holder as PostViewHolder).bind(post)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding, callback)
    }
}

