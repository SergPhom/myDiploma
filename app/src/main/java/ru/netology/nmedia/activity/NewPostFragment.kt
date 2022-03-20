package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel
import com.github.dhaval2404.imagepicker.*
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.SharedViewModel
import javax.inject.Inject


@AndroidEntryPoint
class NewPostFragment: Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val model: SharedViewModel by viewModels(ownerProducer = ::requireParentFragment)

    lateinit var appAuth: AppAuth

    private var fragmentBinding: FragmentNewPostBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {

        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false)
           //*********************************************************** обработка onBack
        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {
                try{
                    viewModel.draft = binding.content.text.toString()
                    findNavController().navigateUp()
                }catch (e: Throwable){
                    println("aaaa $e")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
          //*********************************************************** EDIT post/NEW post

        binding.content.requestFocus()

        binding.content.setText(arguments?.getString("textArg"))

        if(binding.content.text.isNullOrBlank()){
            binding.content.setText(viewModel.draft)
            viewModel.draft = ""
        }
         //*********************************************************** Listeners
        binding.okButton.setOnClickListener {
            viewModel.changeContent(binding.content.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        binding.cancelButton.setOnClickListener {
            with(binding.content){
                viewModel.cancel()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                findNavController().navigateUp()
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
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

        model.selected.observe(viewLifecycleOwner){
            if(it) binding.signOutDialog.visibility = View.VISIBLE
        }

        binding.signOutDialogCancel.setOnClickListener {
            binding.signOutDialog.visibility = View.GONE
        }

        binding.signOutButton.setOnClickListener {
           appAuth.removeAuth()
            with(binding.content){
                viewModel.cancel()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}

