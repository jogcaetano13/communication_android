package com.joel.communication.helpers

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@InternalCoroutinesApi
@FlowPreview
class TestFlow<T>(
    private val source: Flow<T>
) : AbstractFlow<T>() {

    private val mutableEmissions: MutableList<T> = mutableListOf()

    // Tag for this [TestCollector] that is used by the DSL
    var tag: String = this::class.java.simpleName

    /**
     * All emissions of the collected [Flow].
     */
    val emissions: List<T> = mutableEmissions

    /**
     * Error of the collected [Flow].
     */
    var error: Throwable? = null
        private set

    /**
     * Completion of the collected [Flow].
     */
    var completed: Boolean = false
        private set

    @Suppress("TooGenericExceptionCaught")
    override suspend fun collectSafely(collector: FlowCollector<T>) {
        try {
            source.collect {
                mutableEmissions.add(it)
                collector.emit(it)
            }
        } catch (throwable: Throwable) {
            error = throwable
        } finally {
            completed = true
        }
    }

    /**
     * Resets [emissions], [error] and [completed].
     */
    fun reset() {
        mutableEmissions.clear()
        error = null
        completed = false
    }
}