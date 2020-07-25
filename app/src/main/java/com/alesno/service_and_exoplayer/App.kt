package com.alesno.service_and_exoplayer

import android.app.Application
import com.alesno.service_and_exoplayer.repository.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    module {
                        single {
                            Repository()
                        }
                        viewModel {
                            val some = get<Repository>()
                            PlayerViewModel(some)
                        }
                    }
                )
            )
        }
    }
}