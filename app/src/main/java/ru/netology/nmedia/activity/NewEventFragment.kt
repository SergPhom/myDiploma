package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.enumeration.EventType
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewEventBinding
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.EventViewModel
import ru.netology.nmedia.viewmodel.MapViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@AndroidEntryPoint
class NewEventFragment: Fragment() {

    private val viewModel: EventViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val mapViewModel: MapViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var fragmentBinding: FragmentNewEventBinding? = null
    lateinit var binding: FragmentNewEventBinding

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewEventBinding.inflate(
            inflater, container,false
        )
        binding.calendar.visibility = View.GONE

        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        var date = LocalDate.of(1,1,1)


        if(viewModel.datetime.value != OffsetDateTime.MIN){
            binding.date.setText(formatter.format(viewModel.datetime.value?.toLocalDate()))
        }
        binding.content.setText(viewModel.edited.value?.content)
        if(mapViewModel.coords.value != LatLng(55.751999, 37.617734)){
            binding.place.isChecked = true
        }
        when(viewModel.type.value){
            EventType.NONE -> {}
            EventType.OFFLINE -> binding.online.visibility = View.GONE
            EventType.ONLINE -> binding.offline.visibility = View.GONE
        }

        binding.okButton.setOnClickListener {
            if (viewModel.type.value == EventType.NONE
                || viewModel.datetime.value == OffsetDateTime.MIN
            ) {
                println("new event ${viewModel.type.value == EventType.NONE}  ${viewModel.datetime.value == OffsetDateTime.MIN}")
                Toast.makeText(activity, R.string.job_warning, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else{
                viewModel.changeEvent(
                    content = binding.content.text.toString(),
                    link = binding.link.text.toString(),
                    coords = when(mapViewModel.coords.value){
                        LatLng(55.751999, 37.617734) -> null
                        else -> mapViewModel.coords.value?.latitude?.let { it1 ->
                            mapViewModel.coords.value?.longitude?.let { it2 ->
                                Coordinates(
                                    it1,
                                    it2
                                )
                            }
                        }
                    })
            }
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        binding.date?.setOnClickListener {
            binding.calendar.visibility = View.VISIBLE
        }

        binding.calendar.setOnDateChangeListener { _, i, i2, i3 ->
            date = LocalDate.of(i,i2+1,i3)
            binding.calendar.visibility = View.GONE
            binding.date?.setText(formatter.format(date))
            binding.timer.visibility = View.VISIBLE

        }

        binding.setTime.setOnClickListener {
            viewModel.datetime.value = date
                .atTime(binding.timer.hour,binding.timer.minute,58,1000000).atOffset(ZoneOffset.UTC)
            println("datetime is ${viewModel.datetime.value}")

            binding.timer.visibility = View.GONE
        }

        binding.online.setOnClickListener {
            binding.offline.visibility = View.GONE
            binding.link.visibility = View.VISIBLE
            viewModel.type.value = EventType.ONLINE
        }

        binding.offline.setOnClickListener {
            binding.online.visibility = View.GONE
            binding.place.visibility = View.VISIBLE
            viewModel.type.value = EventType.OFFLINE
        }

        binding.place.setOnClickListener {
            findNavController().navigate(R.id.action_newEventFragment_to_mapsFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        viewModel.edited.value = viewModel.edited.value
            ?.copy(  link = binding.link.text.toString(),
                content = binding.content.text.toString())
        fragmentBinding = null
        super.onDestroyView()
    }
}