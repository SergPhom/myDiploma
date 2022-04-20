package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.EventCallback
import ru.netology.nmedia.adapter.EventsAdapter
import ru.netology.nmedia.databinding.FragmentEventBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.EventViewModel


@AndroidEntryPoint
class EventFragment: Fragment() {

    private val viewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentEventBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = EventsAdapter(object : EventCallback {

            override fun onLiked(event: Event) {

            }

            override fun onShared(event: Event) {
            }

            override fun onRemove(event: Event) {

            }

            override fun onEdit(event: Event) {
            }

            override fun onPlay(event: Event) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.attachment?.url))
                startActivity(intent)
            }

            override fun onSingleView(event: Event) {

            }

            override fun onSingleViewImageOnly(event: Event){

            }

            override fun onSavingRetry(event: Event){

            }
        })

        binding.eventList.adapter = adapter

        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_eventFragment_to_newEventFragment)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        return binding.root
    }

}