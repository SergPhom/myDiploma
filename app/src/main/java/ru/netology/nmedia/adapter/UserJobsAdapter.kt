package ru.netology.nmedia.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardJobBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.UserJob
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

interface JobCallback {
    fun onRemove(job: UserJob){}
    fun onEdit(job: UserJob){}
}

class UserJobsAdapter(
    private val callback: JobCallback
) : PagingDataAdapter<UserJob, RecyclerView.ViewHolder>(diffCallback) {


    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<UserJob>() {
            override fun areItemsTheSame(oldItem: UserJob, newItem: UserJob): Boolean {
                if (oldItem::class != newItem::class){
                    return false
                }
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserJob, newItem: UserJob): Boolean {
                return oldItem == newItem
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val job = getItem(position)
        if (job != null){
            (holder as JobViewHolder).bind(job)
        }else{
            println("JobAdapter error")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CardJobBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, callback)
    }

}

class JobViewHolder(
    private val binding: CardJobBinding ,
    private val callback: JobCallback
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withZone( ZoneId.systemDefault() )

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(userJob: UserJob){
        binding.apply {
            menu.visibility = if (userJob.isMyJob) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply{
                    inflate(R.menu.menu_post)
                    menu.setGroupVisible(R.id.owned, userJob.isMyJob)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId){
                            R.id.remove -> {
                                callback.onRemove(userJob)
                                true
                            }
                            R.id.edit -> {
                                callback.onEdit(userJob)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            companyName.text = userJob.name
            userPosition.text = userJob.position
            positionStart.text = formatter.format(Instant.ofEpochSecond(userJob.start))
            positionFinish.text = userJob.finish?.let {
                formatter.format( Instant.ofEpochSecond(it) )}  ?: ""
        }
    }
}