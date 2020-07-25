package com.alesno.service_and_exoplayer.domain

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.alesno.service_and_exoplayer.presentation.PlayerService

class PlayerServiceConnection(
    private val repository: IRepository,
    applicationContext: Context
) : IPlayer {

    private var mServiceBinder: PlayerService.ServiceBinder? = null
    private var mMediaController: MediaControllerCompat? = null
    private var mMediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            //Передавать состояния через LiveData
        }
    }

    init {
        applicationContext.bindService(
            Intent(applicationContext, PlayerService::class.java),
            object : ServiceConnection {

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    mServiceBinder = (service as? PlayerService.ServiceBinder).apply {
                        this?.setRepository(repository)
                        mMediaController?.registerCallback(mMediaControllerCallback)
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    mServiceBinder = null

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