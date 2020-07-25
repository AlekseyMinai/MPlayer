package com.alesno.service_and_exoplayer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alesno.service_and_exoplayer.domain.IPlayer
import com.alesno.service_and_exoplayer.domain.IRepository
import com.alesno.service_and_exoplayer.domain.PlayerState
import com.alesno.service_and_exoplayer.domain.Track
import com.alesno.service_and_exoplayer.uiLazy

class PlayerViewModel(
    private val player: IPlayer,
    repository: IRepository
) : ViewModel() {

    val currentState: LiveData<PlayerState> get() = mCurrentState
    val track: LiveData<Track> get() = mTrack
    private val mCurrentState by uiLazy { MutableLiveData<PlayerState>() }
    private val mTrack by uiLazy { MutableLiveData<Track>() }

    init {
        mCurrentState.value = PlayerState.READY
    }

    fun action() {
        when (mCurrentState.value) {
            PlayerState.PLAYING -> stop()
            PlayerState.READY,
            PlayerState.STOPPED -> play()
            PlayerState.LOADED,
            null -> Unit
        }
    }

    private fun play() {
        player.play()
        mCurrentState.value = PlayerState.PLAYING
    }

    private fun stop() {
        player.stop()
        mCurrentState.value = PlayerState.STOPPED
    }

}