package com.example.myapplication.retrointerfaces

import com.example.myapplication.data.RegionBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface VatomincApi {

    companion object {
        const val BASE_URL = "https://api.vi.vatom.network/"
        const val APP_ID = "App-Id: 0acba308-c879-4c4e-92a4-9c53db183ce5"
        const val FORM_DATA = "Content-Type: multipart/form-data"
        const val JSON = "Content-Type: application/json"

        const val OPEN_ID = "https://id.vatominc.com/"
        const val BLOCKV_URL = "https://api.blockv.io"


        const val VatomIncIntegersStart = 0xF54123
        const val ActivityRequestCodeAuthorization = VatomIncIntegersStart + 1
        const val ActivityEndSessionAuthorization = VatomIncIntegersStart + 2
    }

    @Headers(APP_ID)
    @GET
    suspend fun getVatomList(@Url url: String, @Header("Authorization") accessToken: String): Response<ResponseBody>

    @Headers(APP_ID, JSON)
    @POST
    suspend fun getRegionVatoms(@Url url: String, @Header("Authorization") accessToken: String,
                                @Body parms: RegionBody
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST
    suspend fun exchangeTokens(@Url url: String, @Field("grant_type") grantType: String,
                            @Field("client_id") clientId: String,
                            @Field("resource") resource: String,
                            @Field("subject_token_type") tokenType: String,
                            @Field("subject_token") token: String
    ): Response<ResponseBody>

}