package com.dalakoti07.android

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "locations")

suspend fun <T> DataStore<Preferences>.getValue(
    key: Preferences.Key<T>, defaultValue: T
): T {
    return this.data
        .catchAndHandleError()
        .map { preferences -> preferences[key] }
        .firstOrNull() ?: defaultValue
}

fun Flow<Preferences>.catchAndHandleError(): Flow<Preferences> {
    this.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
    return this@catchAndHandleError
}

