package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel


@AndroidEntryPoint
class AuthFragment: Fragment() {
    private val viewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )

        //*********************************************************** onBackPrassed
        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {
                try{
                    findNavController().navigateUp()
                }catch (e: Throwable){
                    println("onBackPressed error is $e")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
        binding.enterButton.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                val result = viewModel.getToken(binding.login.text.toString() , binding.password.text.toString())
                println("AuthFragment result is $result")
                if (result){
                    findNavController().navigateUp()
                } else{
                    AndroidUtils.hideKeyboard(requireView())
                    val toast = Toast.makeText(
                        context,
                        "Пользователя с такими данными не существует",
                        Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER,0,0)
                    toast.show()
                }
            }
        }

        return binding.root
    }
}