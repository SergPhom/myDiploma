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
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.enumeration.EventType
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewEventBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.EventViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@AndroidEntryPoint
class NewEventFragment: Fragment() {

    private val viewModel: EventViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var fragmentBinding: FragmentNewEventBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewEventBinding.inflate(
            inflater, container,false
        )
        var type: EventType? = null
        binding.calendar.visibility = View.GONE

        var date = LocalDate.of(1,1,1)
        var datetime: OffsetDateTime

        binding.okButton.setOnClickListener {
            if (type == null || binding.date.text.isNullOrBlank()) {
                Toast.makeText(activity, R.string.job_warning, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else{
                viewModel.changeEvent(
                    content = binding.content.text.toString(),
                    date = date.toString(),
                    type = type!!,
                    link = binding.link.text.toString(),
                    coords = null)
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
            println("date is $date } ")
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            binding.calendar.visibility = View.GONE
            binding.date?.setText(formatter.format(date))
            binding.timer.visibility = View.VISIBLE
        }
        binding.timer.setOnTimeChangedListener { _, i, i2 ->
            datetime = date.atTime(i,i2,58,555).atOffset(ZoneOffset.UTC)
            //binding.timer.visibility = View.GONE
            println("datetime is $datetime")
        }

        binding.setTime.setOnClickListener {
            binding.timer.visibility = View.GONE
        }

        binding.online.setOnClickListener {
            binding.offline.visibility = View.GONE
            binding.link.visibility = View.VISIBLE
            type = EventType.ONLINE
        }

        binding.offline.setOnClickListener {
            binding.online.visibility = View.GONE
            binding.place.visibility = View.VISIBLE
            type = EventType.OFFLINE
        }
        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}