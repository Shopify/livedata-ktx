/*
 * Copyright (c) 2018 Henry Tao <hi@henrytao.me>.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.henrytao.livedataktx

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Handler

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
fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (t: T?) -> Unit): Removable {
    val removable: RemovableImpl<T> = RemovableImpl(this, Observer { observer(it) })
    observe(owner, removable.observer)
    return removable
}

fun <T> LiveData<T>.observe(observer: (t: T?) -> Unit): Removable {
    val removable: RemovableImpl<T> = RemovableImpl(this, Observer { observer(it) })
    observeForever(removable.observer)
    return removable
}

@Suppress("DEPRECATION")
fun <T> SupportMediatorLiveData<T>.observe(owner: LifecycleOwner, observer: (t: T) -> Unit): Removable {
    val removable: RemovableImpl<T> = RemovableImpl(this, Observer { it?.let(observer) })
    observe(owner, removable.observer)
    return removable
}

@Suppress("DEPRECATION")
fun <T> SupportMediatorLiveData<T>.observe(observer: (t: T) -> Unit): Removable {
    val removable: RemovableImpl<T> = RemovableImpl(this, Observer { it?.let { observer(it) } })
    observeForever(removable.observer)
    return removable
}

/**
 * Supporting classes
 */
private interface MediatorObserver<IN, OUT> {

    fun run(source: LiveData<IN>, mediator: SupportMediatorLiveData<OUT>, value: IN?)
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

private class RemovableImpl<T>(private val liveData: LiveData<T>, val observer: Observer<T>) : Removable {

    override fun removeObserver() {
        liveData.removeObserver(observer)
    }
}