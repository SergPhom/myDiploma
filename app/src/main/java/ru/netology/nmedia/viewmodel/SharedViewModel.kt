package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    val selected = MutableLiveData(false)

    fun select() {
        selected.value = true
    }

    val maxId = MutableLiveData(0L)

    fun maxChange(newMax: Long){
        maxId.value = newMax
    }
}