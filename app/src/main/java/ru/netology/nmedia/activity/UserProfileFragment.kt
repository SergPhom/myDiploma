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
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.JobCallback
import ru.netology.nmedia.adapter.UserJobsAdapter
import ru.netology.nmedia.databinding.FragmentNewEventBinding
import ru.netology.nmedia.databinding.FragmentUserProfileBinding
import ru.netology.nmedia.dto.UserJob
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.UserProfileViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

enum class WhichDate{
    START,
    FINISH,
    NONE
}
@AndroidEntryPoint
class UserProfileFragment: Fragment() {

    private var fragmentBinding: FragmentNewEventBinding? = null

    private val viewModel: UserProfileViewModel by viewModels(
        //ownerProducer = ::requireParentFragment
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var binding = FragmentUserProfileBinding.inflate(
            inflater, container, false
        )
        var startDate: LocalDateTime = LocalDateTime.MIN
        var finishDate: LocalDateTime = LocalDateTime.MIN
        var whichDate: WhichDate = WhichDate.NONE
        var jobId = 0L
        val author =  arguments?.getString("userName")
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        //for adapter
        val adapter = UserJobsAdapter(object : JobCallback{
            override fun onRemove(job: UserJob) {
                viewModel.removeJob(job)
            }
            override fun onEdit(job: UserJob) {
                binding.jobEditCard.visibility = View.VISIBLE
                binding.companyName.setText(job.name)
                binding.position.setText(job.position)
                binding.startDate.setText(formatter.format(LocalDateTime
                    .ofEpochSecond(job.start, 0,
                    ZoneOffset.UTC)))
                startDate = LocalDateTime.ofEpochSecond(job.start,0, ZoneOffset.UTC)
                binding.finishDate.setText(job.finish?.let{formatter.format(LocalDateTime
                    .ofEpochSecond(job.start, 0, ZoneOffset.UTC))} ?: "")
                finishDate =  job.finish?.let {
                    LocalDateTime.ofEpochSecond(it,0, ZoneOffset.UTC)
                } ?: LocalDateTime.MIN
                jobId = job.id

            }
        })
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.refresh.isRefreshing =
                    state.refresh is LoadState.Loading
            }
        }

        //item dividers
        val decorator = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        decorator.setDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.common_google_signin_btn_icon_dark) }!!)
        binding.jobList.addItemDecoration(decorator)

        //initial
        fun clearAll(){
            AndroidUtils.hideKeyboard(requireView())
        }
        binding.calendar.visibility = View.GONE
        binding.author.text = author
        binding.jobList.adapter = adapter

        //fab
        binding.fab.isVisible = viewModel.isMyJobs
        binding.fab.setOnClickListener {
            binding.jobEditCard.visibility = View.VISIBLE
            binding.fab.visibility = View.GONE
        }

        //set Start and Finish dates
        binding.startDate.setOnClickListener {
            binding.calendar.visibility = View.VISIBLE
            binding.jobEditCard.visibility = View.GONE
            whichDate = WhichDate.START
        }
        binding.finishDate.setOnClickListener {
            binding.calendar.visibility = View.VISIBLE
            binding.jobEditCard.visibility = View.GONE
            whichDate = WhichDate.FINISH
        }
        binding.calendar.setOnDateChangeListener {
                _, i, i2, i3 ->
            val date = LocalDate.of(i,i2+1,i3).atTime(0,0,0)
            when(whichDate){
                WhichDate.START ->{
                    startDate = date
                    binding.startDate.setText(formatter.format(startDate))
                }
                WhichDate.FINISH ->{
                    finishDate = date
                    binding.finishDate.setText(formatter.format(finishDate))
                }
            }
            binding.calendar.visibility = View.GONE
            binding.jobEditCard.visibility = View.VISIBLE
        }

        //save and cancel
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
                jobId,
                binding.companyName.text.toString(),
                binding.position.text.toString(),
                startDate.toInstant(ZoneOffset.UTC).epochSecond,
                finishDate.toInstant(ZoneOffset.UTC).epochSecond
            )
            binding.jobEditCard.visibility =View.GONE
            clearAll()
        }
        binding.cancelButton.setOnClickListener {
            clearAll()
        }

        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }


        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}