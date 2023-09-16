package com.dalakoti07.android;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val totalEntries = 14_000

    private val breakEntry = totalEntries/2

    // max window between any `location i` and `location i + 1`
    private val maxWindowSize = longPreferencesKey("maxWindowSize")

    // last location timestamp
    private val lastLocationTimeStamp = longPreferencesKey("lastLocationTimeStamp")

    private val answerWereWhichLocations = stringPreferencesKey("answerWereWhichLocations")

    private suspend fun <T> storeValue(key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

    private suspend fun <T> getData(key: Preferences.Key<T>, defaultValue: T): T {
        return dataStore.getValue(key, defaultValue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            resetAllValuesForNewDay()
            createSomeFakeEntries()
        }
    }

    // todo important for each session
    private suspend fun resetAllValuesForNewDay() {
        storeValue(maxWindowSize, 0)
        storeValue(lastLocationTimeStamp, -1L)

    }

    // 3 seconds for 1 locations
    // 12 hour shift is 12*60*60 = 43200
    // entries in 12 hours is 43200/3 = 14k
    // lets do more of it .......
    // lets test for 30K entries and see how things go
    // and lets assume each entries we get in 200ms
    // let's simulate this
    private suspend fun createSomeFakeEntries() {
        for (i in 1..totalEntries) {
            Log.d(TAG, "i -> $i")
            if (i < breakEntry) {
                delay(100)
                addEntryToPrefs(i)
            }else if(i == breakEntry){
                // a delay of 600 ms
                delay(600)
                addEntryToPrefs(i)
            }
            if (i > breakEntry + 1) {
                delay(100)
                addEntryToPrefs(i)
            }
        }
        // print results
        Log.d(TAG, "bucket: ${getData(maxWindowSize, 0)} and answer -> ${getData(answerWereWhichLocations,"")}")
    }

    // add entry to prefs
    private suspend fun addEntryToPrefs(i: Int) {
        val currentTimeStamp = System.currentTimeMillis()
        val lastTimeStamp = getData(lastLocationTimeStamp, -1L)
        if (lastTimeStamp != -1L) {
            val maxWindowsSize = getData(maxWindowSize, 0)
            val timeDifferenceFromLastLocation = currentTimeStamp - lastTimeStamp
            if (timeDifferenceFromLastLocation >= maxWindowsSize) {
                storeValue(maxWindowSize, timeDifferenceFromLastLocation)
                storeValue(answerWereWhichLocations, "$i and ${i-1}")
            }
        }
        storeValue(lastLocationTimeStamp, currentTimeStamp)
    }
}