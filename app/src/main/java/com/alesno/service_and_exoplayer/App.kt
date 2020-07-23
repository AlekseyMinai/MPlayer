package com.alesno.service_and_exoplayer

import android.app.Application
import com.alesno.service_and_exoplayer.some.Some
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
                            Some()
                        }
                        viewModel {
                            val some = get<Some>()
                            PlayerViewModel(some)
                        }
                    }
                )
            )
        }
    }
}