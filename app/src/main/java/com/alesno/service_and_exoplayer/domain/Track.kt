package com.alesno.service_and_exoplayer.domain

data class Track(
    val coverUrl: String? = null,
    val artist: String,
    val title: String,
    val url: String?
)