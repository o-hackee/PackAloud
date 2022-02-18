package ol.ko.packaloud

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SpeechConfigStore {
    private const val PREFS_NAME = "speech_config"
    val Context.speechConfigPrefsDataStore: DataStore<Preferences> by preferencesDataStore(PREFS_NAME)
}

class SpeechConfigRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val rateKey = stringPreferencesKey("rate")
        val styleKey = stringPreferencesKey("style")
    }
    internal suspend fun saveRate(rate: String) {
        Log.d("OLKO", "save rate")
        dataStore.edit { prefs ->
            prefs[rateKey] = rate
        }
    }
    internal suspend fun saveStyle(style: String) {
        Log.d("OLKO", "save style")
        dataStore.edit { prefs ->
            prefs[styleKey] = style
        }
    }
    internal fun loadRate(): Flow<String?> = dataStore.data.map { prefs ->
        prefs[rateKey]
    }
    internal fun loadStyle(): Flow<String?> = dataStore.data.map { prefs ->
        prefs[styleKey]
    }
}