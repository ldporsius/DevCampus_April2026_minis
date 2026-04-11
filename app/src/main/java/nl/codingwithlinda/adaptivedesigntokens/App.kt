package nl.codingwithlinda.adaptivedesigntokens

import android.app.Application
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.data.di.profileDataModule
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.di.profilePresentationModule
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.di.editingStatusPresentationModule
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.di.readyToTypePresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                profileDataModule,
                profilePresentationModule,
                readyToTypePresentationModule,
                editingStatusPresentationModule,
            )
        }
    }
}