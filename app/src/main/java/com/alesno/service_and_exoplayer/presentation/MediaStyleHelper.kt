package com.alesno.service_and_exoplayer.presentation

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.alesno.service_and_exoplayer.presentation.extensions.artist
import com.alesno.service_and_exoplayer.presentation.extensions.title

object MediaStyleHelper {

    fun from(context: Context, mediaSession: MediaSessionCompat) =
        mediaSession.controller.metadata.let { mediaMetaData ->
            NotificationCompat.Builder(context)
                .setContentText("mediaMetaData.artist")
                .setSubText("mediaMetaData.title")
                .setContentIntent(mediaSession.controller.sessionActivity)
                .setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

}