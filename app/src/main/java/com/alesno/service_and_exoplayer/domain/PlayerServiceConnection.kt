package com.alesno.service_and_exoplayer.domain

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alesno.service_and_exoplayer.presentation.PlayerService
import com.alesno.service_and_exoplayer.presentation.TrackViewState
import com.alesno.service_and_exoplayer.presentation.extensions.albumArtUri
import com.alesno.service_and_exoplayer.presentation.extensions.artist
import com.alesno.service_and_exoplayer.presentation.extensions.title

class PlayerServiceConnection(
    private val repository: IRepository,
    applicationContext: Context
) : IPlayer {

    override val state: LiveData<PlayerState>
        get() = mState
    override val track: LiveData<TrackViewState>
        get() = mTrack

    private val mState by lazy { MutableLiveData<PlayerState>() }
    private val mTrack by lazy { MutableLiveData<TrackViewState>() }
    private var mServiceBinder: PlayerService.ServiceBinder? = null
    private var mMediaController: MediaControllerCompat? = null
    private var mMediaControllerCallback = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            mState.postValue(
                when (state?.state) {
                    PlaybackStateCompat.STATE_PLAYING -> PlayerState.PLAYING
                    else -> PlayerState.STOPPED
                }
            )
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            metadata?.title?.let { mTrack.postValue(metadata.asTrack()) }
        }

    }

    init {
        applicationContext.bindService(
            Intent(applicationContext, PlayerService::class.java),
            object : ServiceConnection {

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    mServiceBinder = (service as? PlayerService.ServiceBinder).apply {
                        this?.let {
                            setRepository(repository)
                            mMediaController =
                                getMediaSessionToken()?.let { sessionToken ->
                                    MediaControllerCompat(
                                        applicationContext,
                                        sessionToken
                                    )
                                }
                            mMediaController?.registerCallback(mMediaControllerCallback)
                        }
                        mState.value = PlayerState.READY
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    mServiceBinder = null
                    mMediaController?.unregisterCallback(mMediaControllerCallback)
                    mMediaController = null
                    Toast.makeText(applicationContext, "disconnect", Toast.LENGTH_SHORT).show()
                }

            },
            Context.BIND_AUTO_CREATE
        )
    }

    override fun play() {
        mMediaController?.transportControls?.play()
    }

    override fun stop() {
        mMediaController?.transportControls?.stop()
    }

}

private fun MediaMetadataCompat?.asTrack() =
    this?.let {
        TrackViewState(
            title = title.orEmpty(),
            artist = artist.orEmpty(),
            coverUrl = albumArtUri
        )
    }
