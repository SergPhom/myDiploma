package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.adapter.JobListAdapter
import ru.netology.nmedia.databinding.FragmentUserProfileBinding


@AndroidEntryPoint
class UserProfileFragment: Fragment() {


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserProfileBinding.inflate(
            inflater,
            container,
            false
        )

        val author =  arguments?.getString("userName")
        val adapter = JobListAdapter()

        binding.author.text = author
        binding.jobList.adapter = adapter

        return binding.root
    }
}