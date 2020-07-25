package com.alesno.service_and_exoplayer.repository

import com.alesno.service_and_exoplayer.R
import com.alesno.service_and_exoplayer.domain.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository : IRepository {

    override fun fetch(): Flow<Track> =
        flow {
            delay(800)
            emit(
                Track(
                    resId = R.raw.hypa_hypa,
                    artist = "Eskimo Callboy",
                    title = "Hypa Hypa",
                    coverId = R.drawable.hypa
                )
            )
        }
}