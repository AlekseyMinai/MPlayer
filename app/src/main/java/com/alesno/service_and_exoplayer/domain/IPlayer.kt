package com.alesno.service_and_exoplayer.domain

import androidx.lifecycle.LiveData
import com.alesno.service_and_exoplayer.presentation.TrackViewState

interface IPlayer {
    val state: LiveData<PlayerState>
    val track: LiveData<TrackViewState>
    fun play()
    fun stop()
}