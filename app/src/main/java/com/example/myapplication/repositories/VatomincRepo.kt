package com.example.myapplication.repositories

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.myapplication.data.Coord
import com.example.myapplication.data.LocationBounds
import com.example.myapplication.data.RegionBody
import com.example.myapplication.retrointerfaces.VatomincApi
import com.example.myapplication.retrointerfaces.VatomincApi.Companion.ActivityEndSessionAuthorization
import com.example.myapplication.retrointerfaces.VatomincApi.Companion.ActivityRequestCodeAuthorization
import com.example.myapplication.retrointerfaces.VatomincApi.Companion.BASE_URL
import com.example.myapplication.retrointerfaces.VatomincApi.Companion.BLOCKV_URL
import com.example.myapplication.retrointerfaces.VatomincApi.Companion.OPEN_ID
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.utils.wrap
import com.example.myapplication.utils.wrap2
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import net.openid.appauth.*
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class VatomincRepo @Inject constructor(
    private val vatomApi: VatomincApi,
    private val app: Application,
    private val userPref: UserPreferences
) {

    /** The base URL for the OpenID server */
    private val oauthServer = Uri.parse("https://id.vatominc.com")
    /** The redirect URL to return to the app */
    private val redirectURL = Uri.parse("com.vatom://auth")
    /** The Client ID to use when authenticating the app */
    private val clientID = "wallet-google"

    /** Generic authorization service */
    private val authService = AuthorizationService(app)
    private lateinit var newAuthState: AuthState

    /** Stored coroutine continuer for the openID login */
    private var authPending: Continuation<AuthorizationResponse>? = null
    private var endSessionPending: Continuation<EndSessionResponse>? = null

    /** Cached authorization state */
    private var authState: AuthState? = null

    suspend fun getVatoms(accessToken: String): ResponseBody? {
        val response = vatomApi.getVatomList("${BASE_URL}v1/user/vatom/inventory", "Bearer $accessToken")
        return if (response.isSuccessful) response.body()!!
        else return null
    }

    suspend fun getRegionVatoms(accessToken: String, locationBounds: LocationBounds): ResponseBody? {
        val response = vatomApi.getRegionVatoms("${BASE_URL}v1/vatom/geodiscover", "Bearer $accessToken",
            RegionBody(Coord(locationBounds.location.southwest.latitude, locationBounds.location.southwest.longitude),
                Coord(locationBounds.location.northeast.latitude, locationBounds.location.northeast.longitude),
                "all",
                10
            )
        )
        return if (response.isSuccessful) response.body()!!
        else return null
    }

    private suspend fun exchangeBlockvToken(): ResponseBody? {
        val token: String = newAuthState.accessToken ?: ""
        val response = vatomApi.exchangeTokens("${OPEN_ID}token", "urn:ietf:params:oauth:grant-type:token-exchange",
            clientID, BLOCKV_URL, "urn:ietf:params:oauth:token-type:access_token",token)
        return if (response.isSuccessful) response.body()
        else return null
    }

    /** Login with OpenID */
    suspend fun loginOpenID(fromActivity: Activity, scopes: Array<String> = arrayOf("openid", "profile", "email", "offline_access")) {

        // Discover service configuration
        val authServiceConfig = wrap<AuthorizationServiceConfiguration> { AuthorizationServiceConfiguration.fetchFromIssuer(oauthServer, it) }

        // Create auth state
        newAuthState = AuthState(authServiceConfig)

        // Build the request
        val request = AuthorizationRequest.Builder(authServiceConfig, clientID, ResponseTypeValues.CODE, redirectURL)
            .setScope(scopes.joinToString(" "))
            .setPrompt("consent")
            .build()

        // Send it
        val authIntent = authService.getAuthorizationRequestIntent(request)
        fromActivity.startActivityForResult(authIntent, ActivityRequestCodeAuthorization)

        // Fetch response, waiting for onActivityResult to be called
        val authResponse = suspendCoroutine<AuthorizationResponse> { cont ->
            authPending = cont
        }

        newAuthState.update(authResponse, null)

        // Request token
        val tokenResponse = wrap<TokenResponse> { authService.performTokenRequest(authResponse.createTokenExchangeRequest(), it) }
        newAuthState.update(tokenResponse, null)

        // Extract BLOCKv tokens from response
        val data = withContext(IO) { exchangeBlockvToken()!!.string() }
        val json = JSONObject(data)
        val refreshToken = json.optString("refresh_token") ?: throw Exception("No BLOCKv refresh token was provided by the OpenID server.")
        val accessToken = json.optString("access_token") ?: throw Exception("No BLOCKv access token was provided by the OpenID server.")

        Log.d("myT", accessToken)
        userPref.updateBlockvTokenData(accessToken)

        // Done, we are now logged in! Save the auth state.
        authState = newAuthState

    }

    /** Called when an activity receives a response */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Check request
        if (requestCode == ActivityRequestCodeAuthorization) {

            // Check for data
            if (data == null) {
                authPending?.resumeWithException(Exception("Login failed, no data returned."))
                authPending = null
                return
            }

            // Process response
            val result = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)
            when {
                ex != null -> authPending?.resumeWithException(ex)
                result == null -> authPending?.resumeWithException(Exception("Login failed, no data returned."))
                else -> authPending?.resume(result)
            }

            // Done
            authPending = null

        } else if (requestCode == ActivityEndSessionAuthorization) {

            // Check for data
            if (data == null) {
                endSessionPending?.resumeWithException(Exception("Logout failed, no data returned."))
                endSessionPending = null
                return
            }

            // Process response
            val result = EndSessionResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)
            when {
                ex != null -> endSessionPending?.resumeWithException(ex)
                result == null -> endSessionPending?.resumeWithException(Exception("Login failed, no data returned."))
                else -> endSessionPending?.resume(result)
            }

            // Done
            endSessionPending = null

        }
    }

    /** Update access token */
    suspend fun updateAccessTokenIfNeeded() {

        // Stop if no auth state
        val authState = authState ?: return

        // Perform auth state update
        try {
            wrap2<String, String> {
                authState.performActionWithFreshTokens(authService, it)
            }
        } catch (e: Exception) {

        }

        // Save the new auth state changes
        this.authState = authState

    }

}