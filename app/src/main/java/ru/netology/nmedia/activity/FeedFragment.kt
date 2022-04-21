package ru.netology.nmedia

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TableRow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.UserWallViewModel
import java.lang.Math.abs
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment: Fragment(){

    private val viewModel: PostViewModel by viewModels(
    ownerProducer = ::requireParentFragment)

    private val userWallViewModel: UserWallViewModel by viewModels(
        ownerProducer = ::requireParentFragment)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {

        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : Callback {
            override fun onLiked(post: Post) {
                if(viewModel.authenticated.value == true) viewModel.onLiked(post)
                else binding.signInDialog.visibility = View.VISIBLE
            }

            override fun onRemove(post: Post) {
                viewModel.onRemove(post)
            }

            override fun onEdit(post: Post) {
                viewModel.onEdit(post)
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                    bundleOf("textArg" to post.content))
            }

            override fun onPlay(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.attachment?.url))
                startActivity(intent)
            }

            override fun onSingleView(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_singlePostFragment,
                    bundleOf("ARG_POST" to post))
            }

            override fun onSingleViewImageOnly(post: Post){
                findNavController().navigate(R.id.action_feedFragment_to_singlePostFragment,
                    bundleOf("ARG_POST" to post.copy(id=0))  )
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSavingRetry(post: Post){
                viewModel.edited.value = post
                viewModel.save()
            }

            override fun onUserProfile(author: String, authorId: Long) {
                findNavController().navigate(R.id.action_feedFragment_to_userProfileFragment,
                    bundleOf("userName" to author, "userId" to authorId)  )
            }

            override fun onUserWall(author: String, authorId: Long) {
                findNavController().navigate(R.id.action_feedFragment_to_userWallFragment,
                bundleOf("USER_ID" to authorId)  )

            }
        })

        binding.list.adapter = adapter
//            .withLoadStateHeaderAndFooter(
//            header = PostLoadingStateAdapter{
//                adapter.retry() },
//            footer = PostLoadingStateAdapter{
//                adapter.retry()
//            }
//        )

            //****************************************************************Observers
//        viewModel.newerCount.observe(viewLifecycleOwner){
//            try {
//                println("FF newer $it")
//                binding.newerPosts.isVisible = it > 0
//                binding.newerPosts.text =  "${getString(R.string.newer_posts)} - "+
//                        " ${viewModel.newerCount.value}"
//            }catch (t: Throwable){ println ("FF error is ${t.stackTrace}")}
//        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.refresh.isRefreshing =
                    state.refresh is LoadState.Loading
            }
        }

        viewModel.authenticated.observe(viewLifecycleOwner){
            adapter.refresh()
        }

            //**************************************************************Listeners
        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }

//        binding.newerPosts.setOnClickListener {
//            binding.newerPosts.isVisible = false
//            viewModel.markNewerPostsViewed()
//        }

//        binding.retryButton.setOnClickListener {
//            viewModel.loadPosts()
//        }

        binding.fab.setOnClickListener {
            if(viewModel.authenticated.value == true){
                viewModel.forAuthenticated()
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else{
                binding.signInDialog.visibility = View.VISIBLE
            }
        }
        binding.signInDialogOk.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_authFragment,
            )
        }
        binding.signInDialogCancel.setOnClickListener{
            binding.signInDialog.visibility = View.GONE
        }

        return binding.root
    }

}