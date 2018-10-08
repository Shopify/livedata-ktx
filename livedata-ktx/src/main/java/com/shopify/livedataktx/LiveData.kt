/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Shopify Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.shopify.livedataktx

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Handler

/**
 * combineWith
 */
private class CombineWith<T, R, S>(private val mapper: (T?, R?) -> S?) : CombinedMediatorObserver<T, R, S> {

    override fun run(firstSource: LiveData<T>, secondSource: LiveData<R>, mediator: SupportMediatorLiveData<S>) {
        try {
            mediator.value = mapper(firstSource.value, secondSource.value)
        } catch (_: NotCombinableException) {
        }
    }
}

fun <T, R, S> LiveData<T>.combineWith(other: LiveData<R>, mapper: (T?, R?) -> S?): LiveData<S> =
        createCombinedMediator(this, other, CombineWith<T, R, S>(mapper))

fun <T, R, S> LiveData<T>.combineWith(other: SupportMediatorLiveData<R>, mapper: (T?, R) -> S?): LiveData<S> =
        createCombinedMediator(this, other, CombineWith<T, R, S>({ t, r ->
            if (r == null) {
                throw NotCombinableException()
            }
            mapper(t, r)
        }))

fun <T, R, S> SupportMediatorLiveData<T>.combineWith(other: LiveData<R>, mapper: (T, R?) -> S?): LiveData<S> =
        createCombinedMediator(this, other, CombineWith<T, R, S>({ t, r ->
            if (t == null) {
                throw NotCombinableException()
            }
            mapper(t, r)
        }))

fun <T, R, S> SupportMediatorLiveData<T>.combineWith(other: SupportMediatorLiveData<R>, mapper: (T, R) -> S?): LiveData<S> =
        createCombinedMediator(this, other, CombineWith<T, R, S>({ t, r ->
            if (t == null || r == null) {
                throw NotCombinableException()
            }
            mapper(t, r)
        }))

/**
 * debounce
 */
private class DebounceExt<T>(private val delayMillis: Long) : MediatorObserver<T, T> {

    private val handler = Handler()
    private var runnable: Runnable? = null

    override fun run(source: LiveData<T>, mediator: SupportMediatorLiveData<T>, value: T?) {
        if (runnable != null) {
            handler.removeCallbacks(runnable)
        }
        runnable = Runnable { mediator.value = value }
        handler.postDelayed(runnable, delayMillis)
    }
}

fun <T> LiveData<T>.debounce(delayMillis: Long): LiveData<T> = createMediator(this, DebounceExt(delayMillis))
fun <T> SupportMediatorLiveData<T>.debounce(delayMillis: Long): SupportMediatorLiveData<T> = createMediator(this, DebounceExt(delayMillis))

/**
 * distinct
 */
private class DistinctExt<T> : MediatorObserver<T, T> {

    override fun run(source: LiveData<T>, mediator: SupportMediatorLiveData<T>, value: T?) {
        if (value != mediator.value) {
            mediator.value = value
        }
    }
}

fun <T> LiveData<T>.distinct(): LiveData<T> = createMediator(this, DistinctExt())
fun <T> SupportMediatorLiveData<T>.distinct(): SupportMediatorLiveData<T> = createMediator(this, DistinctExt())

/**
 * filter
 */
private class FilterExt<T>(private val predicate: (T?) -> Boolean) : MediatorObserver<T, T> {

    override fun run(source: LiveData<T>, mediator: SupportMediatorLiveData<T>, value: T?) {
        if (predicate(value)) {
            mediator.value = value
        }
    }
}

fun <T> LiveData<T>.filter(predicate: (T?) -> Boolean): LiveData<T> = createMediator(this, FilterExt<T>(predicate))
fun <T> SupportMediatorLiveData<T>.filter(predicate: (T) -> Boolean): SupportMediatorLiveData<T> = createMediator(this, FilterExt<T>({
    predicate(it!!)
}))

/**
 * ofType
 */
private class OfTypeExt<T, R>(private val clazz: Class<R>) : MediatorObserver<T, R> {

    override fun run(source: LiveData<T>, mediator: SupportMediatorLiveData<R>, value: T?) {
        if (clazz.isInstance(value)) {
            @Suppress("UNCHECKED_CAST")
            mediator.value = value as R
        }
    }
}

fun <T, R> LiveData<T>.ofType(clazz: Class<R>): LiveData<R> = createMediator(this, OfTypeExt(clazz))

/**
 * first
 */
private class FirstExt<T> : MediatorObserver<T, T> {

    override fun run(source: LiveData<T>, mediator: SupportMediatorLiveData<T>, value: T?) {
        mediator.value = value
        mediator.removeSource(source)
    }
}

fun <T> LiveData<T>.first(): LiveData<T> = createMediator(this, FirstExt())
fun <T> SupportMediatorLiveData<T>.first(): SupportMediatorLiveData<T> = createMediator(this, FirstExt())

/**
 * map
 */
private class MapExt<T, R>(private val mapper: (T?) -> R?) : MediatorObserver<T, R> {

    override fun run(source: LiveData<T>, mediator: SupportMediatorLiveData<R>, value: T?) {
        mediator.value = mapper(value)
    }
}

fun <T, R> LiveData<T>.map(mapper: (T?) -> R?): LiveData<R> = createMediator(this, MapExt<T, R>(mapper))
fun <T, R> SupportMediatorLiveData<T>.map(mapper: (T) -> R): SupportMediatorLiveData<R> = createMediator(this, MapExt<T, R>({
    return@MapExt mapper(it!!)!!
}))

/**
 * nonNull
 */
fun <T> LiveData<T>.nonNull(): SupportMediatorLiveData<T> = createMediator(this, object : MediatorObserver<T, T> {
    override fun run(source: LiveData<T>, mediator: SupportMediatorLiveData<T>, value: T?) {
        value?.let { mediator.value = it }
    }
})

/**
 * observers
 */
inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (t: T?) -> Unit): Removable<T> {
    val removable: Removable<T> = Removable(this, Observer { observer(it) })
    observe(owner, removable.observer)
    return removable
}

inline fun <T> LiveData<T>.observe(crossinline observer: (t: T?) -> Unit): Removable<T> {
    val removable: Removable<T> = Removable(this, Observer { observer(it) })
    observeForever(removable.observer)
    return removable
}

@Suppress("DEPRECATION")
inline fun <T> SupportMediatorLiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (t: T) -> Unit): Removable<T> {
    val removable: Removable<T> = Removable(this, Observer { it?.let(observer) })
    observe(owner, removable.observer)
    return removable
}

@Suppress("DEPRECATION")
inline fun <T> SupportMediatorLiveData<T>.observe(crossinline observer: (t: T) -> Unit): Removable<T> {
    val removable: Removable<T> = Removable(this, Observer { it?.let { observer(it) } })
    observeForever(removable.observer)
    return removable
}

/**
 * Supporting classes
 */
private class NotCombinableException : Exception()

private interface MediatorObserver<IN, OUT> {

    fun run(source: LiveData<IN>, mediator: SupportMediatorLiveData<OUT>, value: IN?)
}

private interface CombinedMediatorObserver<FIRST, SECOND, OUT> {

    fun run(firstSource: LiveData<FIRST>, secondSource: LiveData<SECOND>, mediator: SupportMediatorLiveData<OUT>)
}

private fun <IN, OUT> createMediator(source: LiveData<IN>, observer: MediatorObserver<IN, OUT>): SupportMediatorLiveData<OUT> {
    var isSingle = false
    var versionProvider: (() -> Int)? = null
    if ((source as? SupportMediatorLiveData)?.isSingle == true) {
        isSingle = true
        versionProvider = { source.version }
    }
    val mediator: SupportMediatorLiveData<OUT> = SupportMediatorLiveData(isSingle, versionProvider)
    mediator.addSource(source, Observer { observer.run(source, mediator, it) })
    return mediator
}

private fun <FIRST, SECOND, OUT> createCombinedMediator(firstSource: LiveData<FIRST>, secondSource: LiveData<SECOND>, observer: CombinedMediatorObserver<FIRST, SECOND, OUT>): SupportMediatorLiveData<OUT> {
    return SupportMediatorLiveData<OUT>(false).apply {
        var deferred = false
        addSource(firstSource, {
            if (!deferred) {
                deferred = true
            } else {
                observer.run(firstSource, secondSource, this)
            }
        })
        addSource(secondSource, { observer.run(firstSource, secondSource, this) })
    }
}

