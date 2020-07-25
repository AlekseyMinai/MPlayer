package com.alesno.service_and_exoplayer.domain

data class Track(
    val coverId: Int,
    val artist: String,
    val title: String,
    val resId: Int? = null,
    val url: String
)