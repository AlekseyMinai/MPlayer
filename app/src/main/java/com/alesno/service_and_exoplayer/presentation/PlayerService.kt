package com.alesno.service_and_exoplayer.presentation

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.alesno.service_and_exoplayer.R
import com.alesno.service_and_exoplayer.domain.IRepository
import com.alesno.service_and_exoplayer.presentation.extensions.albumArtUri
import com.alesno.service_and_exoplayer.presentation.extensions.artist
import com.alesno.service_and_exoplayer.presentation.extensions.title
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File

class PlayerService : Service() {

    private var mMediaSession: MediaSessionCompat? = null
    private var mMediaController: MediaControllerCompat? = null

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
    private var mDataSourceFactory: CacheDataSourceFactory? = null

    private val stateBuilder = PlaybackStateCompat.Builder().setActions(
        PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_PAUSE
    )

    private val mMediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            if (!mExoPlayer.playWhenReady) {
                startService(Intent(applicationContext, this@PlayerService.javaClass))
                mRepository?.getNextTrack()
                mMediaSession?.isActive = true
                mExoPlayer.playWhenReady = true
            }
            mMediaSession?.setPlaybackState(
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            val currentState = PlaybackStateCompat.STATE_PLAYING
            refreshNotificationAndForegroundStatus(currentState)
        }

        override fun onStop() {
            if (mExoPlayer.playWhenReady) {
                mExoPlayer.playWhenReady = false
            }

            mMediaSession?.isActive = false
            mMediaSession?.setPlaybackState(
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_STOPPED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            val currentState = PlaybackStateCompat.STATE_STOPPED
            refreshNotificationAndForegroundStatus(currentState)

            stopSelf()
        }

    }

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChanel = NotificationChannel(
                NOTIFICATION_DEFAULT_CHANNEL_ID,
                "Player controls",
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChanel)
        }
        val mediaSession = MediaSessionCompat(this, "PlayerService")
        mMediaController = MediaControllerCompat(applicationContext, mediaSession)
        val intent = Intent(applicationContext, MainActivity::class.java)
        mediaSession.setSessionActivity(PendingIntent.getActivity(applicationContext, 0, intent, 0))
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setCallback(mMediaSessionCallback)

        val mediaButtonIntent = Intent(
            Intent.ACTION_MEDIA_BUTTON, null, applicationContext, MediaButtonReceiver::class.java
        )

        mMediaSession?.setMediaButtonReceiver(
            PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0)
        )

        //TODO можно переделать на работу через MediaSessionConnector и убрать MediaSessionCallback
        /*mMediaSessionConnector = MediaSessionConnector(mediaSession).also { connector ->
            connector.setPlayer(mExoPlayer)
        }*/
        mMediaSession = mediaSession

        val httpDataSourceFactory: DataSource.Factory = OkHttpDataSourceFactory(
            OkHttpClient(),
            Util.getUserAgent(this, getString(R.string.app_name))
        )
        val cache: Cache = SimpleCache(
            File(this.cacheDir.absolutePath + "/exoplayer"),
            LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100)
        )
        mDataSourceFactory = CacheDataSourceFactory(
            cache,
            httpDataSourceFactory,
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mMediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
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
                    title = track?.title
                    artist = track?.artist
                    albumArtUri = track?.url
                }
                prepareToPlay(track?.url ?: "")
            }
        }
    }

    private fun prepareToPlay(url: String) {
        val mediaSource =
            ProgressiveMediaSource.Factory(mDataSourceFactory).createMediaSource(Uri.parse(url))
        mExoPlayer.prepare(mediaSource)
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            if (playWhenReady && playbackState == ExoPlayer.STATE_ENDED) {
                //TODO включаем следующий
                //mMediaSessionCallback.onSkipToNext()
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

    private fun refreshNotificationAndForegroundStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> startForeground(
                NOTIFICATION_ID,
                mMediaSession?.let {
                    MediaStyleHelper.from(applicationContext, it, playbackState, this)
                }
            )
            PlaybackStateCompat.STATE_STOPPED -> {
                mMediaSession?.let {
                    val notification =
                        MediaStyleHelper.from(applicationContext, it, playbackState, this)
                    NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
                }
                stopForeground(false)
            }
            else -> {
                mMediaSession?.let {
                    val notification =
                        MediaStyleHelper.from(applicationContext, it, playbackState, this)
                    NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
                }
                stopForeground(true)
            }
        }
    }

    companion object {

        private const val NOTIFICATION_ID = 404
        private const val NOTIFICATION_DEFAULT_CHANNEL_ID = "default_chanel"

    }
}