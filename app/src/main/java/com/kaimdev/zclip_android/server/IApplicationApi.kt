package com.kaimdev.zclip_android.server

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface IApplicationApi
{
    @POST("/")
    suspend fun sendClipboardContent(
        @Body clipboardContentDto: ClipboardContentDto,
        @QueryMap ipCodeParams: IpCodeParams
    )

    @POST("/request_connection")
    suspend fun requestConnection(@QueryMap ipCodeParams: IpCodeParams)

    @POST("/reply_connection")
    suspend fun replyConnection(@Query("ip") ip: String, @Query("allow") allow: Boolean)

    @GET("/disconnect")
    suspend fun disconnect(@QueryMap ipCodeParams: IpCodeParams)
}