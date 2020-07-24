package com.alesno.service_and_exoplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alesno.service_and_exoplayer.some.ISomeObject

class PlayerViewModel(
    val someObject: ISomeObject
) : ViewModel() {

    val currentState: LiveData<UIState> get() = mCurrentState

    private val mCurrentState by uiLazy { MutableLiveData<UIState>(UIState.STOPPED) }

    fun action() {
        when (mCurrentState.value) {
            UIState.PLAYING -> stop()
            UIState.STOPPED -> play()
        }
    }

    private fun play() {
        mCurrentState.value = UIState.PLAYING
    }

    private fun stop() {
        mCurrentState.value = UIState.STOPPED
    }

    enum class UIState {
        PLAYING, STOPPED, LOADED
    }

}