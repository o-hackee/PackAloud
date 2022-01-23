package ol.ko.packaloud

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object BookmarkDataStore {
    private const val PREFS_NAME = "bookmark"
    private val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(PREFS_NAME)

    fun getInstance(context: Context) = context.prefsDataStore
}

class BookmarkRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        const val PREF_KEY = "bookmark_key"
    }

    internal suspend fun saveBookmark(idx: Int) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(PREF_KEY)] = idx
        }
    }

    internal fun loadBookmark(): Flow<Int?> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREF_KEY)]
    }
}