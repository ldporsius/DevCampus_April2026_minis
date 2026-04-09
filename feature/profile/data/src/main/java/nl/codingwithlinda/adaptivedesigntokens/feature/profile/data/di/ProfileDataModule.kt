package nl.codingwithlinda.adaptivedesigntokens.feature.profile.data.di

import nl.codingwithlinda.adaptivedesigntokens.feature.profile.data.repository.AssetAvatarRepository
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.domain.repository.AvatarRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val profileDataModule = module {
    single { AssetAvatarRepository(androidContext().assets) } bind AvatarRepository::class
}