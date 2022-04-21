package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.UserWallAdapter
import ru.netology.nmedia.databinding.FragmentUserWallBinding
import ru.netology.nmedia.viewmodel.UserWallViewModel

private const val USER_ID = "USER_ID"
val SavedStateHandle.userId: Long
    get() = get(USER_ID) ?: 0

@AndroidEntryPoint
class UserWallFragment: Fragment() {

    private val viewModel: UserWallViewModel by viewModels(ownerProducer = ::requireParentFragment)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserWallBinding.inflate(
            inflater, container, false
        )

        val adapter = UserWallAdapter(object: Callback {

        })
        binding.userJobs.setOnClickListener {
            findNavController().navigate(R.id.action_userWallFragment_to_userProfileFragment)
        }
        binding.list.adapter = adapter
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }
        return binding.root
    }
}