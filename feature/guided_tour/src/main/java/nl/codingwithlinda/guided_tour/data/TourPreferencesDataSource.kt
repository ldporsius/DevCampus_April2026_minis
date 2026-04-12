package nl.codingwithlinda.guided_tour.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tourDataStore: DataStore<Preferences> by preferencesDataStore(name = "tour_prefs")

class TourPreferencesDataSource(private val context: Context) {

    private val TOUR_DONE = booleanPreferencesKey("tour_done")

    val tourDone: Flow<Boolean> = context.tourDataStore.data.map { prefs ->
        prefs[TOUR_DONE] ?: false
    }

    suspend fun setTourDone() {
        context.tourDataStore.edit { prefs ->
            prefs[TOUR_DONE] = true
        }
    }
}