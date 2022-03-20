package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.ActionBarContainer
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.BuildConfig
import javax.inject.Inject

@AndroidEntryPoint
class SinglePostFragment: Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ActionBarContainer.GONE
        val binding = FragmentSinglePostBinding.inflate(
            inflater,
            container,
            false
        )
        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {
                try{
                    findNavController().navigateUp()
                }catch (e: Throwable){
                    println("aaaa $e")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

//        viewModel.data.observe(viewLifecycleOwner) {
//            val post =  arguments?.getParcelable<Post>("ARG_POST")
//            with(binding) {
//                if (post != null) {
//                    if(post.id != 0L){
//                        postContainer.visibility = View.VISIBLE
//                        avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
//                        author.text = post.author
//                        published.text = post.published.toString()
//                        content.text = post.content
//                        shares.text = post.count(post.shares)
//                        viewes.text = "${post.viewes}"
//
//                        likes.text = "${post.likes}"
//                        likes.setIconResource(
//                        if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_likes_24)
//                        likes.isChecked = post.likedByMe
//                        likes.setIconTintResource(R.color.like_button_tint)
//                    //*********************************************************LISTENERS
//                    likes.setOnClickListener {
//                        try {
//                            viewModel.onLiked(post)
//                        }catch (e: Throwable){
//                            println("AAA $e")
//                        }
//                    }
//
//                    shares.setOnClickListener {
//                        viewModel.onShared(post)
//                        shares.isChecked = false
//                        val intent = Intent().apply {
//                            action = Intent.ACTION_SEND
//                            putExtra(Intent.EXTRA_TEXT, post.content)
//                            type = "text/plain"
//                        }
//                        val shareIntent =
//                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                        startActivity(shareIntent)
//                        }
//
//                    menu.setOnClickListener {
//                        PopupMenu(it.context, it).apply {
//                            inflate(R.menu.menu_post)
//                            setOnMenuItemClickListener { item ->
//                                when (item.itemId) {
//                                    R.id.remove -> {
//                                        viewModel.onRemove(post)
//                                        findNavController().navigateUp()
//                                        true
//                                    }
//                                    R.id.edit -> {
//                                        viewModel.onEdit(post)
//                                        val bundle = bundleOf("textArg" to post.content)
//                                        findNavController().navigate(
//                                            R.id.action_singlePostFragment_to_newPostFragment,
//                                            bundle
//                                        )
//                                        true
//                                    }
//                                    else -> false
//                                }
//                            }
//                        }.show()
//                    }
//                    }
//                    //*******************************************************OPTIONS
//                    if (post.attachment != null) {
//                        when (post.attachment.type){
//                            AttachmentType.VIDEO -> {
//                                video.visibility = View.VISIBLE
//                                videoPlay.visibility = View.VISIBLE
//                                video.setOnClickListener {
//                                    val intent = Intent(Intent.ACTION_VIEW,
//                                        Uri.parse("${BuildConfig.BASE_URL}" +
//                                                "/media/${post.attachment.url}"))
//                                    startActivity(intent)
//                                }
//                                videoPlay.setOnClickListener {
//                                    val intent = Intent(Intent.ACTION_VIEW,
//                                        Uri.parse("${BuildConfig.BASE_URL}" +
//                                                "/media/${post.attachment.url}"))
//                                    startActivity(intent)
//                                }
//                            }
//                            AttachmentType.IMAGE ->{
//                                if(post.id == 0L){
//                                    attachmentContainer
//                                        .setBackgroundColor(resources.getColor(R.color.black))
//                                }
//                                imageAttachment.visibility = View.VISIBLE
//                                imageAttachment
//                                    .load("${BuildConfig.BASE_URL}" +
//                                            "/media/${post.attachment.url}")
//                            }
//                        }
//                    }
//                }
//            }
//        }
        return binding.root
    }
}