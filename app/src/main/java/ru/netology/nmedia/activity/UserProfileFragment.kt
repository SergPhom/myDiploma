package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.UserJobsAdapter
import ru.netology.nmedia.databinding.FragmentUserProfileBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.UserProfileViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@AndroidEntryPoint
class UserProfileFragment: Fragment() {

    private val viewModel: UserProfileViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @RequiresApi(Build.VERSION_CODES.O)
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
        val id = arguments?.getLong("userId")
        val adapter = UserJobsAdapter()
        lateinit var startDate: LocalDateTime

        if (id != null) {
            viewModel.id.value = id
            println(" id is ${viewModel.id.value}")
        }
        viewModel.id.observe(viewLifecycleOwner){
            viewModel.isMyId.observe(viewLifecycleOwner){
                println(" isMyId is $it")
                binding.fab.isVisible = it
            }
        }


        fun clearAll(){
            binding.companyName.text.clear()
            binding.position.text.clear()
            binding.startDate.text.clear()
            binding.finishDate.text.clear()
            binding.jobEditCard.visibility = View.GONE
            binding.fab.visibility = View.VISIBLE
            AndroidUtils.hideKeyboard(requireView())
        }
        binding.calendar.visibility = View.GONE

        binding.author.text = author
        binding.jobList.adapter = adapter

        val decorator = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        decorator.setDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.common_google_signin_btn_icon_dark) }!!)
        binding.jobList.addItemDecoration(
            decorator
//            DividerItemDecoration(
//                context,
//                LinearLayoutManager.HORIZONTAL
//            )
        )

        binding.fab.setOnClickListener {
            binding.jobEditCard.visibility = View.VISIBLE
            binding.fab.visibility = View.GONE
        }

        binding.enterButton.setOnClickListener {
            if(binding.companyName.text.isNullOrBlank()
                || binding.position.text.isNullOrBlank()
                || binding.startDate.text.isNullOrBlank()
            ){
                val toast =Toast.makeText(context, R.string.job_warning, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER,0,0)
                toast.show()
                return@setOnClickListener
            }
            viewModel.save(
                binding.companyName.text.toString(),
                binding.position.text.toString(),
                startDate.toInstant(ZoneOffset.UTC).epochSecond
            )
            clearAll()
        }
        binding.cancelButton.setOnClickListener {
            clearAll()
        }

        binding.startDate.setOnClickListener {
            binding.calendar.visibility = View.VISIBLE
            binding.calendar.tag
            binding.jobEditCard.visibility = View.GONE
        }

        binding.calendar.setOnDateChangeListener {
                _, i, i2, i3 ->
            startDate = LocalDate.of(i,i2+1,i3).atTime(0,0,0)

            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

            binding.calendar.visibility = View.GONE
            binding.jobEditCard.visibility = View.VISIBLE
            binding.startDate.setText(formatter.format(startDate))
        }

        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        return binding.root
    }


}