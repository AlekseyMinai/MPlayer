package com.alesno.service_and_exoplayer.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IRepository {
    val tracks: MutableStateFlow<Track?>
    fun getNextTrack()
}