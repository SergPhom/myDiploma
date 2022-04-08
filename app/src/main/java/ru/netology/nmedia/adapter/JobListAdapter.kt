package ru.netology.nmedia.adapter

import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardJobBinding
import ru.netology.nmedia.dto.Job

class JobListAdapter(

) : ListAdapter<Job, JobViewHolder>(diffCallback) {


    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Job>() {
            override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
                if (oldItem::class != newItem::class){
                    return false
                }
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        if (job != null){
            (holder as JobViewHolder).bind(job)
        }else{
            println("JobAdapter error")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(
        LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

}

class JobViewHolder(
    private val binding: CardJobBinding ,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(job: Job){
        binding.apply {

        }
    }
}