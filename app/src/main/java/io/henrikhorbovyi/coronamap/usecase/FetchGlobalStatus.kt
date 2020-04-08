package io.henrikhorbovyi.coronamap.usecase

import io.henrikhorbovyi.coronamap.remote.GlobalStatus
import io.henrikhorbovyi.coronamap.repository.CoronaRepository

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */
class FetchGlobalStatus(
    private val coronaRepository: CoronaRepository
) : UseCase<Unit, GlobalStatus>() {

    override suspend fun execute(type: Unit) {
        try {
            val globalStatus: GlobalStatus = coronaRepository.wholeWorldStatus()
            sender.send(Result.success(globalStatus))
        } catch (error: Exception) {
            sender.send(Result.failure(error))
        }
    }
}