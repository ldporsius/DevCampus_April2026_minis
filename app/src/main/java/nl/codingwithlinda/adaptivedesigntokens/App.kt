package nl.codingwithlinda.adaptivedesigntokens

import android.app.Application
import nl.codingwithlinda.adaptivedesigntokens.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}