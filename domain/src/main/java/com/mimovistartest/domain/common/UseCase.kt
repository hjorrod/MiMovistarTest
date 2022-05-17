package com.mimovistartest.domain.common

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

abstract class UseCaseNoParams<Result> {
    protected abstract suspend fun run(): Flow<Result>
    fun invoke(
        scope: CoroutineScope = GlobalScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onResult: (Result) -> Unit = {}
    ) {
        val job = scope.async(dispatcher) { run() }
        scope.launch(Dispatchers.Main) {
            job.await().collect { result ->
                onResult(result)
            }
        }
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
    protected abstract suspend fun run(params: Params): Flow<Result>
    fun invoke(
        scope: CoroutineScope = GlobalScope,
        params: Params,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onResult: (Result) -> Unit = {}
    ) {
        val job = scope.async(dispatcher) { run(params) }
        scope.launch(Dispatchers.Main) {
            job.await().collect { result ->
                onResult(result)
            }
        }
    }
}