package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
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

        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        var date = LocalDate.of(1,1,1)

        //initials
        binding.calendar.visibility = View.GONE
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

        //date and time set
        binding.date.setOnClickListener {
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
                .atTime(binding.timer.hour,binding.timer.minute,58,1000000)
                .atOffset(ZoneOffset.UTC)
            binding.timer.visibility = View.GONE
        }

        //set type
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

        //photo functions block
        val pickPhotoLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                        "image/jpg"
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }
        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }
        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }
        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        //save and cancel
        binding.okButton.setOnClickListener {
            if (viewModel.type.value == EventType.NONE
                || viewModel.datetime.value == OffsetDateTime.MIN
            ) {
                Toast.makeText(activity,
                    R.string.job_warning,
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else{
                viewModel.changeEvent(
                    content = binding.content.text.toString(),
                    link = binding.link.text.toString(),
                    coords = when(mapViewModel.coords.value){
                        LatLng(55.751999, 37.617734) -> null
                        else -> mapViewModel.coords.value?.let { it ->
                                Coordinates(it.latitude!!, it.longitude!!)
                        }
                    }
                )
            }
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        binding.cancelButton.setOnClickListener {
            fragmentBinding = null
            viewModel.clearEvent()
            findNavController().navigateUp()
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