package com.joel.jlibtemplate

import android.app.Application
import com.joel.communication.enums.LogLevel
import com.joel.jlibtemplate.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
            modules(
                clientModule {
                    baseUrl = BASE_URL
                    logLevel = LogLevel.Basic
                },
                databaseModule(),
                repositoriesModule(),
                communicationDispatcher(),
                viewModelModule()
            )
        }
    }
}