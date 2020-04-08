package io.henrikhorbovyi.coronamap.remote

import retrofit2.http.GET

/**
 * .:.:.:. Created by @henrikhorbovyi on 4/1/20 .:.:.:.
 */
interface CoronaServiceV2 {

    // Get JHU CSSE Data. This includes confirmed cases, deaths, recovered, and coordinates.
    @GET("jhucsse")
    suspend fun wholeWorld(): List<GlobalInfo>

    @GET("historical/all")
    suspend fun historical(): HistoricalResponse

}