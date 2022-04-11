package ru.netology.nmedia.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardJobBinding
import ru.netology.nmedia.dto.UserJob
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class UserJobsAdapter(

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
        return JobViewHolder(binding)
    }

}

class JobViewHolder(
    private val binding: CardJobBinding ,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withZone( ZoneId.systemDefault() )

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(userJob: UserJob){
        println("UserJob adapter is $userJob")
        binding.apply {
            companyName.text = userJob.name
            position.text = userJob.position
            positionStart.text = formatter.format(Instant.ofEpochSecond(userJob.start))
        }
    }
}