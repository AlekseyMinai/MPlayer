package com.alesno.service_and_exoplayer.domain

import com.alesno.service_and_exoplayer.domain.Track
import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun fetch(): Flow<Track>
}