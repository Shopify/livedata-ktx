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
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer

open class SupportMediatorLiveData<T>(internal val isSingle: Boolean = false, private val versionProvider: (() -> Int)? = null) : MediatorLiveData<T>() {

    private var _version = 0
    internal val version: Int get() = versionProvider?.let { it() } ?: _version

    @Deprecated("Use observe extension")
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        val observerVersion = version
        super.observe(owner, Observer {
            if (!isSingle || observerVersion < version) {
                observer.onChanged(it)
            }
        })
    }

    @Deprecated("Use observe extension without LifecycleOwner")
    override fun observeForever(observer: Observer<T>) {
        super.observeForever(observer)
    }

    override fun setValue(value: T?) {
        _version++
        super.setValue(value)
    }
}