package io.henrikhorbovyi.coronamap.usecase

import io.henrikhorbovyi.coronamap.remote.GlobalInfo
import io.henrikhorbovyi.coronamap.repository.CoronaRepository
import java.lang.Exception

/**
 * .:.:.:. Created by @henrikhorbovyi on 4/1/20 .:.:.:.
 */
class FetchGlobalInfo(
    private val coronaRepository: CoronaRepository
) : UseCase<Unit, List<GlobalInfo>>() {

    override suspend fun execute(type: Unit) {
        try {
            val result = coronaRepository.wholeWorld()
            sender.send(Result.success(result))
        } catch (e: Exception) {
            sender.send(Result.failure(e))
        }
    }
}