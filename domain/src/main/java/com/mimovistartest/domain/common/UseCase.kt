package com.mimovistartest.domain.common

import kotlinx.coroutines.*

abstract class UseCaseNoParams<Result> {
    protected abstract suspend fun run(): Result
    fun invoke(
        scope: CoroutineScope = GlobalScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onResult: (Result) -> Unit = {}
    ) {
        val job = scope.async(dispatcher) { run() }
        scope.launch(Dispatchers.Main) { onResult(job.await()) }
    }
}

abstract class UseCaseNoResult<Params> {
    protected abstract suspend fun run(params: Params)
    fun invoke(
        scope: CoroutineScope = GlobalScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        params: Params,
    ) {
        scope.launch(dispatcher) { run(params) }
    }
}

abstract class UseCase<Params, Result> {
    protected abstract suspend fun run(params: Params): Result
    fun invoke(
        scope: CoroutineScope = GlobalScope,
        params: Params,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onResult: (Result) -> Unit = {}
    ) {
        val job = scope.async(dispatcher) { run(params) }
        scope.launch(Dispatchers.Main) { onResult(job.await()) }
    }
}