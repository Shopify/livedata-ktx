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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference

open class PublishLiveDataKtx<T> internal constructor(
    private val source: LiveData<*>?,
    private val isPublish: Boolean = (source as? PublishLiveDataKtx<*>)?.isPublish == true
) : MediatorLiveDataKtx<T>() {

    private var _version = 0
    private var version: Int
        get() = if ((source as? PublishLiveDataKtx<*>)?.isPublish == true) source.version else _version
        set(newVersion) {
            if ((source as? PublishLiveDataKtx<*>)?.isPublish == true) {
                source.version = newVersion
            } else {
                _version = newVersion
            }
        }

    private val observerList =
        mutableListOf<Pair<WeakReference<Observer<in T>>, WeakReference<Observer<T>>>>()

    constructor() : this(null, true)

    override fun setValue(value: T) {
        version++
        super.setValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val observeSinceVersion = version
        val wrapper = Observer<T> {
            if (!isPublish || version > observeSinceVersion) {
                observer.onChanged(it)
            }
        }
        observerList.add(Pair(WeakReference(observer), WeakReference(wrapper)))
        super.observe(owner, wrapper)
    }

    override fun removeObserver(observer: Observer<in T>) {
        val target = observerList.find { it.first.get() == observer }?.second?.get() ?: observer
        super.removeObserver(target)
    }
}