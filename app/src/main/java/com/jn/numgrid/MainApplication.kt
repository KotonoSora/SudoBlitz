package com.jn.numgrid

import android.app.Application
import local.sudoku.ui.di.AppContainer
import local.sudoku.ui.di.AppContainerHost
import local.sudoku.ui.di.DefaultAppContainer

class MainApplication : Application(), AppContainerHost {
    override lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this, BuildConfig.DEBUG)
    }
}
