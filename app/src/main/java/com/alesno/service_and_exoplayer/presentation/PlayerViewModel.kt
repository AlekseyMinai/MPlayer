package com.alesno.service_and_exoplayer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alesno.service_and_exoplayer.domain.IPlayer
import com.alesno.service_and_exoplayer.domain.PlayerState

class PlayerViewModel(
    private val player: IPlayer
) : ViewModel() {

    val currentState: LiveData<PlayerState> get() = player.state
    val track: LiveData<TrackViewState> get() = player.track

    fun action() {
        when (currentState.value) {
            PlayerState.PLAYING -> stop()
            PlayerState.READY,
            PlayerState.STOPPED -> play()
            PlayerState.LOADED,
            null -> Unit
        }
    }

    private fun play() {
        player.play()
    }

    private fun stop() {
        player.stop()
    }

}