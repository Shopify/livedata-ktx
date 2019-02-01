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

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

open class MediatorLiveDataKtx<T> : MutableLiveDataKtx<T>() {

    private val mediatorLiveData = CustomMediatorLiveData<T>()

    @MainThread
    fun <S> addSource(source: LiveData<S>, @NonNull onChanged: Observer<in S>) {
        mediatorLiveData.addSource(source, onChanged)
    }

    @MainThread
    fun <S> removeSource(toRemote: LiveData<S>) {
        mediatorLiveData.removeSource(toRemote)
    }

    override fun onActive() {
        super.onActive()
        mediatorLiveData.onActive()
    }

    override fun onInactive() {
        super.onInactive()
        mediatorLiveData.onInactive()
    }

    private class CustomMediatorLiveData<T> : MediatorLiveData<T>() {

        public override fun onActive() {
            super.onActive()
        }

        public override fun onInactive() {
            super.onInactive()
        }
    }
}