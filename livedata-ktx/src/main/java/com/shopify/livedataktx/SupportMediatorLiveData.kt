/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2018 Shopify Inc.
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package com.shopify.livedataktx

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer

private typealias ObserverWrappers<T> = MutableList<Pair<Observer<T>, Observer<T>>>

private fun <T> ObserverWrappers<T>.get(observer: Observer<T>): Observer<T>? =
    indexOfFirst { it.first == observer }
        .takeUnless { it < 0 }
        ?.let { this[it].second }

private fun <T> ObserverWrappers<T>.getOrElse(
    observer: Observer<T>,
    wrapper: Observer<T>
): Observer<T> = get(observer) ?: wrapper

private fun <T> ObserverWrappers<T>.putIfAbsent(observer: Observer<T>, wrapper: Observer<T>) {
    val index = indexOfFirst { it.first == observer }
    if (index < 0) {
        add(Pair(observer, wrapper))
    }
}

private fun <T> ObserverWrappers<T>.removeIfPresent(observer: Observer<T>) {
    val index = indexOfFirst { it.first == observer }
    if (index >= 0) {
        removeAt(index)
    }
}

open class SupportMediatorLiveData<T>(
    internal val isSingle: Boolean = false,
    private val versionProvider: (() -> Int)? = null
) : MediatorLiveData<T>() {

    private val observerWrappers: ObserverWrappers<T> = mutableListOf()

    private var _version = 0
    internal val version: Int get() = versionProvider?.let { it() } ?: _version

    @Deprecated("Use observe extension")
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        val observerVersion = version
        val wrapper = observerWrappers.getOrElse(observer, Observer {
            if (!isSingle || observerVersion < version) {
                observer.onChanged(it)
            }
        })
        observerWrappers.putIfAbsent(observer, wrapper)
        super.observe(owner, wrapper)
    }

    @Deprecated("Use observe extension without LifecycleOwner")
    override fun observeForever(observer: Observer<T>) {
        val observerVersion = version
        val wrapper = observerWrappers.getOrElse(observer, Observer {
            if (!isSingle || observerVersion < version) {
                observer.onChanged(it)
            }
        })
        observerWrappers.putIfAbsent(observer, wrapper)
        super.observeForever(wrapper)
    }

    override fun setValue(value: T?) {
        _version++
        super.setValue(value)
    }

    override fun removeObserver(observer: Observer<T>) {
        observerWrappers.get(observer)?.let { super.removeObserver(it) }
        observerWrappers.removeIfPresent(observer)
    }
}