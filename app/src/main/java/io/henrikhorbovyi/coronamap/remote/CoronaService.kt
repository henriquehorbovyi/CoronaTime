package io.henrikhorbovyi.coronamap.remote

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */

interface CoronaService {

    @GET("/all")
    suspend fun wholeWorldStatus() : GlobalStatus

    @GET("/countries/{country}")
    suspend fun getByCountry(@Path("country") country : String) : CountryInfo

    @GET("/countries")
    suspend fun allCountries() : List<CountryInfo>
}