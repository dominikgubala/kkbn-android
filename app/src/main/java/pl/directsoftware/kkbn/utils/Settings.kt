package pl.directsoftware.kkbn.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pl.directsoftware.kkbn.App
import pl.directsoftware.kkbn.defs.Setting

object Settings {

    private val Context.dataStore by preferencesDataStore(name = "settings")

    private suspend fun write(key: Preferences.Key<String>, value: String) {
        App.context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    private fun read(key: Preferences.Key<String>): Flow<String?> {
        return App.context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    suspend fun writeString(setting: Setting, value: String) {
        write(stringPreferencesKey(setting.name), value)
    }

    suspend fun writeInt(setting: Setting, value: Int) {
        write(stringPreferencesKey(setting.name), value.toString())
    }

    suspend fun readString(setting: Setting): String? {
        return read(stringPreferencesKey(setting.name)).first()
    }

    suspend fun readInt(setting: Setting): Int? {
        return read(stringPreferencesKey(setting.name)).first()?.toIntOrNull()
    }
}