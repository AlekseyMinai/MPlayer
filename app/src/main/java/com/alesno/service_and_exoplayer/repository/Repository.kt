package com.alesno.service_and_exoplayer.repository

import com.alesno.service_and_exoplayer.domain.IRepository
import com.alesno.service_and_exoplayer.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow

class Repository : IRepository {

    private val remoteData = mutableListOf(
        Track(
            artist = "Eskimo Callboy",
            title = "Hypa Hypa",
            coverUrl = "https://i.ytimg.com/vi/75Mw8r5gW8E/maxresdefault.jpg",
            url = "https://firebasestorage.googleapis.com/v0/b/testfirebase-a2d24.appspot.com/o/hypa_hypa.mp3?alt=media&token=2b6a0ab5-8748-4dd7-bad4-cc5476856708"
        )
    )
    private var mNextTrackIndex = 0

    override val tracks: MutableStateFlow<Track> = MutableStateFlow(
        Track( //todo нужно значение по умолчанию, придумать что-то, чтобы инициировать при получении первого значения
            title = "test",
            artist = "flow",
            url = null
        )
    )

    override fun getNextTrack() {
        if (mNextTrackIndex == remoteData.size) mNextTrackIndex = 0
        else mNextTrackIndex++
        tracks.value = remoteData[mNextTrackIndex]
    }

}