package com.alesno.service_and_exoplayer.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

interface IRepository {
    @ExperimentalCoroutinesApi
    val tracks: MutableStateFlow<Track?>
    fun getNextTrack()
}