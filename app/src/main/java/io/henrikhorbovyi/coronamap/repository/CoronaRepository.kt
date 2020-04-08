package io.henrikhorbovyi.coronamap.repository

import io.henrikhorbovyi.coronamap.remote.GlobalStatus
import io.henrikhorbovyi.coronamap.remote.GlobalInfo
import io.henrikhorbovyi.coronamap.remote.Historical

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */
interface CoronaRepository {
    suspend fun wholeWorldStatus(): GlobalStatus
    suspend fun wholeWorld(): List<GlobalInfo>
    suspend fun historical(): Historical
}