package com.alesno.service_and_exoplayer.domain

import kotlinx.coroutines.flow.StateFlow

interface IRepository {
    val tracks: StateFlow<Track>
    fun getNextTrack()
}