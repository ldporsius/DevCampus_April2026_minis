package nl.codingwithlinda.adaptivedesigntokens

import android.app.Application
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.data.di.profileDataModule
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.di.profilePresentationModule
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.di.editingStatusPresentationModule
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.di.readyToTypePresentationModule
import nl.codingwithlinda.cloud_photo_upload.di.cloudPhotoModule
import nl.codingwithlinda.guided_tour.di.guidedTourModule
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nl.codingwithlinda.guided_tour.data.TourPreferencesDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.GlobalContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val appModule = module {
    single { (androidApplication() as App).applicationScope }
    viewModelOf(::MainViewModel)
}

class App : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                profileDataModule,
                profilePresentationModule,
                readyToTypePresentationModule,
                editingStatusPresentationModule,
                cloudPhotoModule,
                guidedTourModule
            )
        }

        val factory = GlobalContext.get().get<WorkerFactory>()
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(factory)
                .build()
        )

        applicationScope.launch {
            TourPreferencesDataSource(this@App).setTourDone(false)
        }
    }
}