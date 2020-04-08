package io.henrikhorbovyi.coronamap.usecase

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */
abstract class UseCase<in Params, R> : CoroutineScope {

    private val parentJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + parentJob

    protected val sender = Channel<Result<R>>()
    val receiver: ReceiveChannel<Result<R>> = sender

    abstract suspend fun execute(type: Params)

    fun invoke(type: Params): Job {
        return launch {
            println("COROUTINE DEBUG: ${this.coroutineContext}")
            execute(type)
        }
    }

    fun close() {
        sender.close()
        parentJob.cancel()
    }
}

operator fun <R> UseCase<Unit, R>.invoke() = invoke(Unit)