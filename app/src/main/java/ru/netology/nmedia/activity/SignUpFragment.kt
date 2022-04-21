package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignUpModel


@AndroidEntryPoint
class SignUpFragment: Fragment() {

    private val viewModel: SignUpModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false)

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
                        println("URI IS $uri")
                        viewModel.changePhoto(uri, uri?.toFile())

                    }
                }
            }

        viewModel.photo.observe(viewLifecycleOwner){
            if (it.uri == null) {
                binding.avatar.visibility = View.VISIBLE
                return@observe
            }
            binding.avatar.visibility = View.VISIBLE
            binding.avatar.setImageURI(it.uri)
        }
        binding.avatar.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .maxResultSize(100,100)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                        "image/jpg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.enterButton.setOnClickListener {

            val upload = viewModel.photo.value?.file
            viewModel.registrate(binding.login.text.toString(),
                binding.password.text.toString(),
                binding.name.text.toString(),
                upload?.asRequestBody()?.let { it1 ->
                    MultipartBody.Part.createFormData(
                        "file", upload?.name, it1
                    )
                }
            )
            findNavController().navigateUp()
        }
        return binding.root
    }
}