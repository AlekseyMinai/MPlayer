package com.alesno.service_and_exoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
            Observer<PlayerViewModel.UIState>(::updateUI)
        )
    }

    private fun updateUI(state: PlayerViewModel.UIState) {
        val imageRes = when (state) {
            PlayerViewModel.UIState.PLAYING -> R.drawable.ic_baseline_stop_24
            PlayerViewModel.UIState.STOPPED -> R.drawable.ic_baseline_play_arrow_24
            else -> return
        }
        context?.let { actionImage.setImageDrawable(ContextCompat.getDrawable(it, imageRes)) }
    }

    companion object {

        fun newInstance() = PlayerFragment()

    }
}