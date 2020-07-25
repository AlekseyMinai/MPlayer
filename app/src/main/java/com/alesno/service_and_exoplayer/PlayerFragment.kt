package com.alesno.service_and_exoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alesno.service_and_exoplayer.domain.PlayerState
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
        mViewModel.currentState.observe(
            viewLifecycleOwner,
            Observer<PlayerState>(::updateUI)
        )
    }

    private fun updateUI(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> {
                progressBar.isVisible = false
                actionImage.isVisible = true
                context?.let {
                    actionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_baseline_stop_24
                        )
                    )
                }
            }
            PlayerState.STOPPED -> {
                progressBar.isVisible = false
                actionImage.isVisible = true
                context?.let {
                    actionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_baseline_play_arrow_24
                        )
                    )
                }
            }
            PlayerState.LOADED -> {
                progressBar.isVisible = true
                actionImage.isVisible = false
            }
        }

    }

    companion object {

        fun newInstance() = PlayerFragment()

    }
}