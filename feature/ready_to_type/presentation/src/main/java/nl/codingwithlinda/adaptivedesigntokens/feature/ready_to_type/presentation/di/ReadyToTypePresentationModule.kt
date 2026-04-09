package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.di

import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain.PinValidator
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.ReadyToTypeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val readyToTypePresentationModule = module {
    singleOf(::PinValidator)
    viewModelOf(::ReadyToTypeViewModel)
}