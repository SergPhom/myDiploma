package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.EventCallback
import ru.netology.nmedia.adapter.EventsAdapter
import ru.netology.nmedia.databinding.FragmentEventBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.viewmodel.EventViewModel
import ru.netology.nmedia.viewmodel.MapViewModel

@AndroidEntryPoint
class EventFragment: Fragment() {

    private val viewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment)
    private val mapViewModel: MapViewModel by viewModels(
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

        //adapter block
        val adapter = EventsAdapter(object : EventCallback {

            override fun onLiked(event: Event) {
                if(viewModel.authenticated.value == true) viewModel.likeEvent(event)
                else binding.signInDialog.visibility = View.VISIBLE
            }

            override fun onPlace(event: Event) {
                mapViewModel.coords.value = event.coords
                    ?.let { LatLng(it.lat!!, it.longitude!!) }
                findNavController().navigate(R.id.action_eventFragment_to_mapsFragment)
            }

            override fun onRemove(event: Event) {
                viewModel.removeEvent(event)
            }

            override fun onPlay(event: Event) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.attachment?.url))
                startActivity(intent)
            }

            override fun onSingleView(event: Event) {

            }

            override fun onSingleViewImageOnly(event: Event){

            }
        })
        binding.eventList.adapter = adapter
        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }
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

        //fab
        binding.fab.setOnClickListener {
            if(viewModel.authenticated.value == true){
                findNavController().navigate(R.id.action_eventFragment_to_newEventFragment)
            } else{
                binding.signInDialog.visibility = View.VISIBLE
            }
        }

        //signIn dialog
        binding.signInDialogOk.setOnClickListener {
            findNavController().navigate(
                R.id.action_eventFragment_to_authFragment,
            )
        }
        binding.signInDialogCancel.setOnClickListener{
            binding.signInDialog.visibility = View.GONE
        }

        return binding.root
    }

}