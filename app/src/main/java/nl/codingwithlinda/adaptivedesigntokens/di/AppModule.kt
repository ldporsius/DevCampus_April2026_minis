package nl.codingwithlinda.adaptivedesigntokens.di

import nl.codingwithlinda.adaptivedesigntokens.data.assets.AndroidAssetImageProvider
import nl.codingwithlinda.adaptivedesigntokens.data.assets.AssetImageProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { AndroidAssetImageProvider(androidContext().assets) } bind AssetImageProvider::class
}