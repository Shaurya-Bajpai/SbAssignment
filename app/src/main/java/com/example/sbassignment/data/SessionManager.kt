package com.example.sbassignment.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session")

class SessionManager(
    private val context: Context
) {

    companion object {
        private val USER_TYPE = stringPreferencesKey("user_type")
    }

    val userType: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[USER_TYPE]
        }


    suspend fun login(type: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TYPE] = type
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_TYPE)
        }
    }
}