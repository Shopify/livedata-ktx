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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

object Extension {

    fun <IN, OUT> create(
        source: LiveDataKtx<IN>,
        operator: Operator<IN, OUT>
    ): MediatorLiveDataKtx<OUT> = toSwitchMap(source) {
        PublishLiveDataKtx<OUT>(source).apply {
            operator.run(
                this,
                it
            )
        }
    }
}

/**
 * filter
 */
private class FilterOperator<T>(val predicate: (T) -> Boolean) : Operator<T, T> {

    override fun run(output: MediatorLiveDataKtx<T>, value: T) {
        if (predicate.invoke(value)) {
            output.value = value
        }
    }
}

fun <T> LiveDataKtx<T>.filter(predicate: (T) -> Boolean): LiveDataKtx<T> =
    Extension.create(this, FilterOperator(predicate))

fun <T> MutableLiveDataKtx<T>.filter(predicate: (T) -> Boolean): MutableLiveDataKtx<T> =
    Extension.create(this, FilterOperator(predicate))

fun <T> MediatorLiveDataKtx<T>.filter(predicate: (T) -> Boolean): MediatorLiveDataKtx<T> =
    Extension.create(this, FilterOperator(predicate))

/**
 * map
 */
private class MapOperator<T, R>(val mapper: (T) -> R) : Operator<T, R> {

    override fun run(output: MediatorLiveDataKtx<R>, value: T) {
        output.value = mapper.invoke(value)
    }
}

fun <T, R> LiveDataKtx<T>.map(mapper: (T) -> R): LiveDataKtx<R> =
    Extension.create(this, MapOperator(mapper))

fun <T, R> MutableLiveDataKtx<T>.map(mapper: (T) -> R): MutableLiveDataKtx<R> =
    Extension.create(this, MapOperator(mapper))

fun <T, R> MediatorLiveDataKtx<T>.map(mapper: (T) -> R): MediatorLiveDataKtx<R> =
    Extension.create(this, MapOperator(mapper))

/**
 * switchMap
 */
private fun <T, R> toSwitchMap(
    source: LiveData<T>,
    func: (T) -> LiveDataKtx<R>
): MediatorLiveDataKtx<R> = toKtx(Transformations.switchMap(source) { func.invoke(it) })

fun <T, R> LiveDataKtx<T>.switchMap(func: (T) -> LiveDataKtx<R>): LiveDataKtx<R> =
    toSwitchMap(this, func)

fun <T, R> MutableLiveDataKtx<T>.switchMap(func: (T) -> LiveDataKtx<R>): MutableLiveDataKtx<R> =
    toSwitchMap(this, func)

fun <T, R> MediatorLiveDataKtx<T>.switchMap(func: (T) -> LiveDataKtx<R>): MediatorLiveDataKtx<R> =
    toSwitchMap(this, func)

/**
 * toKtx
 */
private fun <T> toKtx(source: LiveData<T>): MediatorLiveDataKtx<T> {
    val output = MediatorLiveDataKtx<T>()
    output.addSource(source, Observer { value -> value?.let { output.value = value } })
    return output
}

private fun <T> toNullableKtx(source: LiveData<T>): MediatorLiveDataKtx<T?> {
    val output = MediatorLiveDataKtx<T?>()
    output.addSource(source, Observer { value -> output.value = value })
    return output
}

fun <T : Any> LiveData<T>.toKtx(): LiveDataKtx<T> = toKtx(this)

fun <T : Any> MutableLiveData<T>.toKtx(): MutableLiveDataKtx<T> = toKtx(this)

fun <T : Any> MediatorLiveData<T>.toKtx(): MediatorLiveDataKtx<T> = toKtx(this)

@JvmName("toKtxNullable")
fun <T : Any?> LiveData<T>.toKtx(): LiveDataKtx<T?> = toNullableKtx(this)

@JvmName("toKtxNullable")
fun <T : Any?> MutableLiveData<T>.toKtx(): MutableLiveDataKtx<T?> = toNullableKtx(this)

@JvmName("toKtxNullable")
fun <T : Any?> MediatorLiveData<T>.toKtx(): MediatorLiveDataKtx<T?> = toNullableKtx(this)

@Deprecated("Use toKtx function", ReplaceWith("toKtx()"), DeprecationLevel.WARNING)
fun <T> LiveData<T>.toNullableKtx(): LiveDataKtx<T?> = toNullableKtx(this)

@Deprecated("Use toKtx function", ReplaceWith("toKtx()"), DeprecationLevel.WARNING)
fun <T> MutableLiveData<T>.toNullableKtx(): MutableLiveDataKtx<T?> = toNullableKtx(this)

@Deprecated("Use toKtx function", ReplaceWith("toKtx()"), DeprecationLevel.WARNING)
fun <T> MediatorLiveData<T>.toNullableKtx(): MediatorLiveDataKtx<T?> = toNullableKtx(this)
