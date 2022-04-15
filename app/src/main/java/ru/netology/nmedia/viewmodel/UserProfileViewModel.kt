package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.UserJob
import ru.netology.nmedia.repository.JobRepository
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val repository: JobRepository
):ViewModel() {

    var id: MutableLiveData<Long> = MutableLiveData(0L)

    var isMyId = appAuth
        .authStateFlow.map {
        println("auth id is ${it.id} and my id is ${id.value}")
            it.id == id.value }
        .asLiveData(Dispatchers.Default)


    private val cashed = repository.data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<UserJob>> = cashed

    fun save(companyName: String,
                    position: String,
                    startDate: Long,
                    finishDate: Long? = null,
                    link: String? = null
    ) = viewModelScope.launch {
       val job = UserJob(
           id = 0,
           name = companyName,
           position = position,
           start = startDate,
           finish = finishDate,
           link = link
       )
       println("job is - $job")
        try {
            repository.saveJob(job)
        }catch (t: Throwable){
            println("UserProfile error is $t")
        }
    }

}
