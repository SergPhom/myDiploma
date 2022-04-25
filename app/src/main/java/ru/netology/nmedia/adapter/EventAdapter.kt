package ru.netology.nmedia.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardEventBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadAvatar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


interface EventCallback {
    fun onLiked(event: Event){}
    fun onPlace(event: Event){}
    fun onRemove(event: Event){}
    fun onEdit(event: Event){}
    fun onPlay(event: Event){}
    fun onSingleView(event: Event){}
    fun onSingleViewImageOnly(event: Event){}
}

class EventsAdapter (
    private val callback: EventCallback
): PagingDataAdapter<Event, RecyclerView.ViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                if (oldItem::class != newItem::class){
                    return false
                }
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        if (event != null) {
            (holder as EventViewHolder).bind(event)
        }else {
            println("EventAdapter error")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CardEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, callback)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val callback: EventCallback
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withZone( ZoneId.systemDefault() )

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(event: Event) {
        binding.apply {
            place.isVisible = event.coords != null
            place.setOnClickListener {
                callback.onPlace(event)
            }
            event.authorAvatar?.let { avatar.loadAvatar(it) }
            author.text = "${event.author}  ${event.id}"
            published.text = formatter.format(Instant.parse(event.published))
            content.text = event.content

            likes.text = "${event.likeOwnerIds.size}"
            likes.setIconResource(
                if (event.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_likes_24
            )
            likes.isChecked = event.likedByMe
            likes.setIconTintResource(R.color.like_button_tint)
            likes.setOnClickListener {
                callback.onLiked(event)
            }

            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withZone( ZoneId.systemDefault() )

            val date = Instant.parse(event.datetime).epochSecond
            val now = Instant.now().epochSecond
            val diff = (date-now)
            val hours = (diff / 3600L)
            val days = (hours / 24L)
            datetime.text = formatter.format(Instant.parse(event.datetime))
            datetimeToGo.text = "$days   days"

            menu.visibility = if (event.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply{
                    inflate(R.menu.menu_post)
                    menu.setGroupVisible(R.id.owned, event.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId){
                            R.id.remove -> {
                                callback.onRemove(event)
                                true
                            }
                            R.id.edit -> {
                                callback.onEdit(event)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            video.setOnClickListener {
                callback.onPlay(event)
            }

            videoPlay.setOnClickListener {
                callback.onPlay(event)
            }
            author.setOnClickListener{ callback.onSingleView(event)}
            avatar.setOnClickListener{ callback.onSingleView(event)}
            content.setOnClickListener{ callback.onSingleView(event)}
            published.setOnClickListener{ callback.onSingleView(event)}
            imageAttachment.setOnClickListener { callback.onSingleViewImageOnly(event) }

            if (event.attachment != null) {
                when (event.attachment.type){
                    AttachmentType.VIDEO -> videoGroup.visibility = View.VISIBLE
                    AttachmentType.IMAGE ->{
                        imageAttachment.visibility = View.VISIBLE
                        imageAttachment.load("${BuildConfig.BASE_URL}/media/${event.attachment.url}")
                    }
                }
            }else{
                videoGroup.visibility = View.GONE
                imageAttachment.visibility = View.GONE
            }
        }
    }
}