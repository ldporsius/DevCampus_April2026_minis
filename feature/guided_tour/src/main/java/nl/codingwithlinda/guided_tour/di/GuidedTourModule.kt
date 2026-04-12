package nl.codingwithlinda.guided_tour.di

import nl.codingwithlinda.guided_tour.data.TourPreferencesDataSource
import nl.codingwithlinda.guided_tour.presentation.TourViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val guidedTourModule = module {
    single { TourPreferencesDataSource(androidContext()) }
    viewModelOf(::TourViewModel)
}