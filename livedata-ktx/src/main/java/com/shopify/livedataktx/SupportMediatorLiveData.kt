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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference

open class SupportMediatorLiveData<T>(internal val isSingle: Boolean = false, private val versionProvider: (() -> Int)? = null) : MediatorLiveData<T>() {

    private var _version = 0
    internal val version: Int get() = versionProvider?.let { it() } ?: _version

    private val observerList = mutableListOf<Pair<WeakReference<Observer<in T>>, WeakReference<Observer<T>>>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val observerVersion = version
        val wrapper = Observer<T> {
            if (!isSingle || observerVersion < version) {
                observer.onChanged(it)
            }
        }
        observerList.add(Pair(WeakReference(observer), WeakReference(wrapper)))
        super.observe(owner, wrapper)
    }

    override fun setValue(value: T?) {
        _version++
        super.setValue(value)
    }

    override fun removeObserver(observer: Observer<in T>) {
        val target = observerList.find { it.first.get() == observer }?.second?.get() ?: observer
        super.removeObserver(target)
    }
}