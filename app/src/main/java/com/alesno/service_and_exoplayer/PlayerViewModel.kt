package com.alesno.service_and_exoplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alesno.service_and_exoplayer.domain.PlayerState
import com.alesno.service_and_exoplayer.repository.IRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerViewModel(
    repository: IRepository
) : ViewModel() {

    val currentState: LiveData<PlayerState> get() = mCurrentState
    private val mCurrentState by uiLazy { MutableLiveData<PlayerState>() }

    init {
        mCurrentState.value = PlayerState.LOADED
        viewModelScope.launch {
            repository
                .fetch()
                .collect {
                    mCurrentState.value = PlayerState.STOPPED
                }
        }
    }

    fun action() {
        when (mCurrentState.value) {
            PlayerState.PLAYING -> stop()
            PlayerState.STOPPED -> play()
            PlayerState.LOADED,
            null -> Unit
        }
    }

    private fun play() {
        mCurrentState.value = PlayerState.PLAYING
    }

    private fun stop() {
        mCurrentState.value = PlayerState.STOPPED
    }

}