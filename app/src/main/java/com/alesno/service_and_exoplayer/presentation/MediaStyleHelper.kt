package com.alesno.service_and_exoplayer.presentation

import android.app.Notification
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.session.MediaButtonReceiver
import com.alesno.service_and_exoplayer.R
import com.alesno.service_and_exoplayer.presentation.extensions.artist
import com.alesno.service_and_exoplayer.presentation.extensions.title

object MediaStyleHelper {

    private const val NOTIFICATION_DEFAULT_CHANEL_ID = "default_chanel"

    fun from(
        context: Context,
        mediaSession:
        MediaSessionCompat,
        playbackState: Int,
        serviceContext: Context
    ): Notification =
        NotificationCompat.Builder(context)
            .setContentTitle(mediaSession.controller.metadata.artist)
            .setSubText(mediaSession.controller.metadata.title)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setLargeIcon(
                ContextCompat.getDrawable(context, R.drawable.hypa)?.toBitmap(300, 200)
            ) //TODO подгружать заранее из сети
            .setContentIntent(mediaSession.controller.sessionActivity)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).apply {
                if (playbackState == PlaybackStateCompat.STATE_PLAYING)
                    addAction(
                        NotificationCompat.Action(
                            R.drawable.ic_baseline_stop_24,
                            "Stop",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                serviceContext,
                                PlaybackStateCompat.ACTION_STOP
                            )
                        )
                    )
                else
                    addAction(
                        NotificationCompat.Action(
                            R.drawable.ic_baseline_play_arrow_24,
                            "Play",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                serviceContext,
                                PlaybackStateCompat.ACTION_PLAY
                            )
                        )
                    )
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                serviceContext,
                                PlaybackStateCompat.ACTION_STOP
                            )
                        )
                        .setMediaSession(mediaSession.sessionToken)
                )
                setSmallIcon(R.mipmap.ic_launcher)
                color = ContextCompat.getColor(serviceContext, R.color.colorPrimary)
                setShowWhen(false)
                priority = NotificationCompat.PRIORITY_HIGH
                setOnlyAlertOnce(true)
                setChannelId(NOTIFICATION_DEFAULT_CHANEL_ID)
            }.build()

}