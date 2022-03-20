package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignUpModel
import javax.inject.Inject

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
        binding.enterButton.setOnClickListener {
            viewModel.registrate(binding.login.text.toString(),
                binding.password.text.toString(),
                binding.name.text.toString())
            findNavController().navigateUp()
        }
        return binding.root
    }
}