package com.alesno.service_and_exoplayer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.alesno.service_and_exoplayer.R
import com.alesno.service_and_exoplayer.domain.PlayerState
import com.alesno.service_and_exoplayer.domain.Track
import kotlinx.android.synthetic.main.fragment_player.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private val mViewModel: PlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        action.setOnClickListener {
            mViewModel.action()
        }
        mViewModel.currentState.observeWith(::updateState)
        mViewModel.track.observeWith(::updateTrack)
    }

    private fun updateState(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> {
                progressBar.isVisible = false
                actionImage.isVisible = true
                actionImage.setImageDrawable(R.drawable.ic_baseline_stop_24)
            }
            PlayerState.READY,
            PlayerState.STOPPED -> {
                progressBar.isVisible = false
                actionImage.isVisible = true
                actionImage.setImageDrawable(R.drawable.ic_baseline_play_arrow_24)
            }
            PlayerState.LOADED -> {
                progressBar.isVisible = true
                actionImage.isVisible = false
            }
        }
    }

    private fun updateTrack(track: Track) {
       // cover.setImageDrawable(track.coverId)
        artist.text = track.artist
        title.text = track.title
    }

    companion object {

        fun newInstance() = PlayerFragment()

    }

    private fun <T> LiveData<T>.observeWith(func: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer { func(it) })
    }

    private fun ImageView.setImageDrawable(@DrawableRes imageRes: Int?) {
        if (imageRes == null) return
        setImageDrawable(ContextCompat.getDrawable(context, imageRes))
    }

}