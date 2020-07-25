package com.alesno.service_and_exoplayer.presentation

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.alesno.service_and_exoplayer.domain.IRepository

class PlayerService : Service() {

    //private val metaBuilder = Media

    private val mMediaSession = MediaSessionCompat(this, "PlayerService")
    private val mMediaController = MediaControllerCompat(applicationContext, mMediaSession)
    private var mRepository: IRepository? = null

    /*private val mExoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            addListener()
        }
    }*/

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            super.onPlay()
        }

        override fun onStop() {
            super.onStop()
        }


    }

    override fun onCreate() {
        super.onCreate()

        val activity = Intent(applicationContext, MainActivity::class.java).also { intent ->
            mMediaSession.setSessionActivity(
                PendingIntent.getActivity(applicationContext, 0, intent, 0)
            )
        }
    }

    override fun onBind(intent: Intent?) = ServiceBinder()


    inner class ServiceBinder : Binder() {

        fun setRepository(repository: IRepository) {
            mRepository = repository
        }

        fun getMediaSessionToken() = mMediaSession.sessionToken

    }
}