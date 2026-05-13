package com.shaalevikas.app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

object SessionKeys {
    val ROLE  = stringPreferencesKey("role")
    val UID   = stringPreferencesKey("uid")
    val NAME  = stringPreferencesKey("name")
    val CITY  = stringPreferencesKey("city")
}

class SessionManager(private val context: Context) {

    val role: Flow<String> = context.dataStore.data.map { it[SessionKeys.ROLE] ?: "" }
    val uid:  Flow<String> = context.dataStore.data.map { it[SessionKeys.UID]  ?: "" }
    val name: Flow<String> = context.dataStore.data.map { it[SessionKeys.NAME] ?: "" }
    val city: Flow<String> = context.dataStore.data.map { it[SessionKeys.CITY] ?: "" }

    suspend fun saveAlumni(uid: String, name: String, city: String) {
        context.dataStore.edit {
            it[SessionKeys.ROLE] = "alumni"
            it[SessionKeys.UID]  = uid
            it[SessionKeys.NAME] = name
            it[SessionKeys.CITY] = city
        }
    }

    suspend fun saveAdmin(uid: String) {
        context.dataStore.edit {
            it[SessionKeys.ROLE] = "admin"
            it[SessionKeys.UID]  = uid
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
