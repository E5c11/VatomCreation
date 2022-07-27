package com.example.myapplication.data.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.persistence.UserPreferences.PreferenceKeys.BLOCKV_ACCESS_TOKEN
import com.example.myapplication.data.persistence.UserPreferences.PreferenceKeys.VATOMINC_ACCESS_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class TokenDetails(val vatomInc: String, val blockvToken: String)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_details")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext app: Context) {

    private val userData: DataStore<Preferences> = app.dataStore

    val userPref = userData.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else throw it
        }.map { preferences ->
            val blockvToken = preferences[BLOCKV_ACCESS_TOKEN] ?: "guest_token"
            val vatomIncToken = preferences[VATOMINC_ACCESS_TOKEN] ?: "guest_token"
            TokenDetails(vatomIncToken, blockvToken)
        }

    suspend fun updateBlockvTokenData(accessToken: String) {
        userData.edit { preferences ->
            preferences[BLOCKV_ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun updateVatomincTokenData(accessToken: String) {
        userData.edit { preferences ->
            preferences[VATOMINC_ACCESS_TOKEN] = accessToken
        }
    }

    private object PreferenceKeys {
        val BLOCKV_ACCESS_TOKEN = stringPreferencesKey("blockV_access_token")
        val VATOMINC_ACCESS_TOKEN = stringPreferencesKey("vatominc_access_token")
    }
}