package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.UserWallAdapter
import ru.netology.nmedia.databinding.FragmentUserWallBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.view.loadAvatar
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.UserWallViewModel

private const val USER_ID = "USER_ID"
private const val USER_AVATAR = "USER_AVATAR"
private const val USER_NAME = "USER_NAME"
val SavedStateHandle.userId: Long
    get() = get(USER_ID) ?: 0
val SavedStateHandle.userAvatar: String
    get() = get(USER_AVATAR) ?: ""
val SavedStateHandle.userName: String
    get() = get(USER_NAME) ?: ""


@AndroidEntryPoint
class UserWallFragment: Fragment() {

    private val viewModel: UserWallViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {
                try{
                    findNavController().navigateUp()
                }catch (e: Throwable){
                    println("OnBackPressed error is $e")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
        val binding = FragmentUserWallBinding.inflate(
            inflater, container, false
        )

        val id = arguments?.getLong(USER_ID)
        val adapter = UserWallAdapter(object: Callback {
            override fun onLiked(post: Post) {
                viewModel.likePost(post)
            }
        })

        binding.avatar.loadAvatar(viewModel.avatar())
        binding.author.setText(viewModel.userName())
        binding.userJobs.setOnClickListener {
            findNavController()
                .navigate(R.id.action_userWallFragment_to_userProfileFragment,
                    bundleOf("USER_ID" to id)
                )
        }
        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }
        binding.list.adapter = adapter
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.refresh.isRefreshing =
                    state.refresh is LoadState.Loading
            }
        }

        return binding.root
    }
}