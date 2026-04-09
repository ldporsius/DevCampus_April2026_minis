package nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.di

import nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilePresentationModule = module {
    viewModelOf(::ProfileViewModel)
}