package io.henrikhorbovyi.coronamap.usecase

import io.henrikhorbovyi.coronamap.remote.Historical
import io.henrikhorbovyi.coronamap.repository.CoronaRepository

/**
 * .:.:.:. Created by @henrikhorbovyi on 4/2/20 .:.:.:.
 */
class FetchHistoricalUseCase(
    private val coronaRepository: CoronaRepository
) : UseCase<Unit, Historical>() {

    override suspend fun execute(type: Unit) {
        try {
            val historical: Historical = coronaRepository.historical()
            sender.send(Result.success(historical))
        } catch (e: Exception) {
            sender.send(Result.failure(e))
        }
    }
}