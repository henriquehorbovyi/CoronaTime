package io.henrikhorbovyi.coronamap.remote

import com.google.android.gms.maps.model.LatLng

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */

/* API V1 */
data class GlobalStatus(
    val cases: Int,
    val deaths: Int,
    val recovered: Int
)

data class CountryInfo(
    val active: Int,
    val cases: Int,
    val casesPerOneMillion: Int,
    val country: String,
    val critical: Int,
    val deaths: Int,
    val deathsPerOneMillion: Double,
    val recovered: Int,
    val todayCases: Int,
    val todayDeaths: Int,
    val updated: Long
)

/* API V2 */
data class GlobalInfo(
    val coordinates: Coordinates,
    val country: String,
    val province: String?,
    val stats: Status,
    val updatedAt: String
)

data class Status(
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int
)

data class Coordinates(
    val latitude: String,
    val longitude: String
)

fun Coordinates?.asLatLng(): LatLng? {
    return if (this != null)
        if (latitude.isNotEmpty() && longitude.isNotEmpty())
            LatLng(latitude.toDouble(), longitude.toDouble())
        else null
    else
        null
}


data class HistoricalResponse(
    val cases: Any,
    val deaths: Any,
    val recovered: Any
)

fun HistoricalResponse.asHistorical(): Historical {
    val mappedCases: List<Cases> = cases.toString()
        .splitToSequence(",")
        .map {
            val cleared = it
                .replace("[{}]".toRegex(), "")
                .replace(" ", "")

            val splited = cleared.split("=")
            Cases(splited.first(), splited.last().toDouble())
        }.toList()

    val mappedDeaths: List<Deaths> = deaths.toString()
        .splitToSequence(",")
        .map {
            val cleared = it
                .replace("[{}]".toRegex(), "")
                .replace(" ", "")

            val splited = cleared.split("=")
            Deaths(splited.first(), splited.last().toDouble())
        }.toList()

    val mappedRecovered: List<Recovered> = recovered.toString()
        .splitToSequence(",")
        .map {
            val splittedData = it
                .replace("[{}]".toRegex(), "")
                .replace(" ", "")
                .split("=")

            Recovered(splittedData.first(), splittedData.last().toDouble())
        }.toList()

    return Historical(
        cases = mappedCases,
        deaths = mappedDeaths,
        recovered = mappedRecovered
    )
}

data class Historical(
    val cases: List<Cases>,
    val deaths: List<Deaths>,
    val recovered: List<Recovered>
)

data class Cases(val date: String, val amount: Double)
data class Deaths(val date: String, val amount: Double)
data class Recovered(val date: String, val amount: Double)




