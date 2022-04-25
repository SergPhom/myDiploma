package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.activity.userId
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.UserJob
import ru.netology.nmedia.repository.JobRepository
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    appAuth: AppAuth,
    private val repository: JobRepository
):ViewModel() {

    fun userId(): Long{
        return savedStateHandle.userId
    }

    val isMyJobs = appAuth.authStateFlow.value.id == userId()

    private val cashed = repository.userProfileData(userId())
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<UserJob>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            cashed.map { userJobs ->
                userJobs.map { job ->
                    job.copy(isMyJob = myId == userId())
                }
            }
        }

    fun save(id: Long,
             companyName: String,
             position: String,
             startDate: Long,
             finishDate: Long? = null,
             link: String? = null
    ) = viewModelScope.launch {
        val job = UserJob(
            id = id,
            name = companyName,
            position = position,
            start = startDate,
            finish = finishDate,
            link = link)
        try {
            repository.saveJob(job)
        }catch (t: Throwable){
            println("UserProfile error is $t")
        }
    }

    fun removeJob(job: UserJob){
        viewModelScope.launch {
            try {
                repository.removeJob(job)
            } catch (t: Throwable){
                println("Job remove error is $t")
            }
        }
    }
}
