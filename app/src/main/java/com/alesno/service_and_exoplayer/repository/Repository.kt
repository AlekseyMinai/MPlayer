package com.alesno.service_and_exoplayer.repository

import com.alesno.service_and_exoplayer.R
import com.alesno.service_and_exoplayer.domain.IRepository
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
                    artist = "Eskimo Callboy",
                    title = "Hypa Hypa",
                    coverId = R.drawable.hypa,
                    url = "https://firebasestorage.googleapis.com/v0/b/testfirebase-a2d24.appspot.com/o/hypa_hypa.mp3?alt=media&token=2b6a0ab5-8748-4dd7-bad4-cc5476856708"
                )
            )
        }
}