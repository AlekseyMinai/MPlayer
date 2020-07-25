package com.alesno.service_and_exoplayer.di

import android.content.Context
import com.alesno.service_and_exoplayer.domain.PlayerServiceConnection
import com.alesno.service_and_exoplayer.presentation.PlayerViewModel
import com.alesno.service_and_exoplayer.repository.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

object ApplicationComponent {

    fun init(applicationContext: Context) {
        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    module {
                        single {
                            Repository()
                        }
                        single {
                            PlayerServiceConnection(
                                get<Repository>(),
                                applicationContext
                            )
                        }
                        viewModel {
                            val repository = get<Repository>()
                            val serviceConnector = get<PlayerServiceConnection>()
                            PlayerViewModel(
                                player = serviceConnector,
                                repository = repository
                            )
                        }
                    }
                )
            )
        }
    }
}