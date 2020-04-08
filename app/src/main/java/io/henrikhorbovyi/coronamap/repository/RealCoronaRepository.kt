package io.henrikhorbovyi.coronamap.repository

import io.henrikhorbovyi.coronamap.remote.asHistorical
import io.henrikhorbovyi.coronamap.remote.CoronaService
import io.henrikhorbovyi.coronamap.remote.CoronaServiceV2
import io.henrikhorbovyi.coronamap.remote.GlobalStatus
import io.henrikhorbovyi.coronamap.remote.GlobalInfo
import io.henrikhorbovyi.coronamap.remote.Historical
import io.henrikhorbovyi.coronamap.remote.HistoricalResponse

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */
class RealCoronaRepository(
    private val coronaService: CoronaService,
    private val coronaServiceV2: CoronaServiceV2
) : CoronaRepository {
    override suspend fun wholeWorldStatus(): GlobalStatus {
        return coronaService.wholeWorldStatus()
    }

    override suspend fun wholeWorld(): List<GlobalInfo> {
        return coronaServiceV2.wholeWorld()
    }

    override suspend fun historical(): Historical {
        val response: HistoricalResponse = coronaServiceV2.historical()
        return response.asHistorical()
    }
}
