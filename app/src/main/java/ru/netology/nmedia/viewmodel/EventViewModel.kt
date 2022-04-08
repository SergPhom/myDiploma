package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.repository.EventRepository
import javax.inject.Inject
import kotlin.random.Random


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val appAuth: AppAuth,
): ViewModel() {

    private val cashed = repository.data
        .cachedIn(viewModelScope)
    val data: Flow<PagingData<Event>> = cashed
}