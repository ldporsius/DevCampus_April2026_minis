package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.di

import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.EditingStatusViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val editingStatusPresentationModule = module {
    viewModelOf(::EditingStatusViewModel)
}