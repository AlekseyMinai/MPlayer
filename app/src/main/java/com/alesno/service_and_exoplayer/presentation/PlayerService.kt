package com.alesno.service_and_exoplayer.presentation

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.alesno.service_and_exoplayer.domain.IRepository
import com.alesno.service_and_exoplayer.presentation.extensions.albumArtUri
import com.alesno.service_and_exoplayer.presentation.extensions.artist
import com.alesno.service_and_exoplayer.presentation.extensions.title
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerService : Service() {

    //private val metaBuilder = Media

    private var mMediaSession: MediaSessionCompat? = null
    private var mMediaController: MediaControllerCompat? = null
    private lateinit var mMediaSessionConnector: MediaSessionConnector
    private var mRepository: IRepository? = null
    private val mMediaMetaData = MediaMetadataCompat.Builder()
    private var mLoadDataJob: Job? = null

    private val mAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val mPlayerEventListener = PlayerEventListener()
    private val mExoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            setAudioAttributes(mAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(mPlayerEventListener)
        }
    }

    private val mMediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            super.onPlay()
        }

        override fun onStop() {
            super.onStop()
        }

    }

    override fun onCreate() {
        super.onCreate()
        val mediaSession = MediaSessionCompat(this, "PlayerService")
        mMediaController = MediaControllerCompat(applicationContext, mediaSession)
        val intent = Intent(applicationContext, MainActivity::class.java)
        mediaSession.setSessionActivity(PendingIntent.getActivity(applicationContext, 0, intent, 0))
        mediaSession.setCallback(mMediaSessionCallback)


        mMediaSessionConnector = MediaSessionConnector(mediaSession).also { connector ->
            connector.setPlayer(mExoPlayer)
        }
        mMediaSession = mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaSession?.release()
        mLoadDataJob?.cancel()
        mExoPlayer.removeListener(mPlayerEventListener)
        mExoPlayer.release()
    }

    override fun onBind(intent: Intent?) = ServiceBinder()

    private fun loadData(repository: IRepository) {
        mLoadDataJob = GlobalScope.launch {
            repository.tracks.collect { track ->
                mMediaMetaData.apply {
                    title = track.title
                    artist = track.artist
                    albumArtUri = track.url
                }
            }
        }
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            if (playWhenReady && playbackState == ExoPlayer.STATE_ENDED) {
                //TODO включаем следующий
                mMediaSessionCallback.onSkipToNext()
            }
        }

    }

    inner class ServiceBinder : Binder() {

        fun setRepository(repository: IRepository) {
            mRepository = repository
            loadData(repository)
        }

        fun getMediaSessionToken() = mMediaSession?.sessionToken

    }
}