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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.MutableLiveData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LiveDataTest : LifecycleOwner {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private lateinit var lifecycleRegistry: LifecycleRegistry

    @Before
    fun setup() {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @After
    fun teardown() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    @Test
    fun distinct() {
        val liveData: MutableLiveData<Boolean> = MutableLiveData()
        val actuals: MutableList<Boolean?> = mutableListOf()
        val observer: (t: Boolean?) -> Unit = { actuals.add(it) }
        liveData
                .distinct()
                .observe(this, observer)

        liveData.value = true
        liveData.value = false
        liveData.value = false
        liveData.value = true

        val expecteds = mutableListOf(true, false, true)
        assertEquals(expecteds, actuals)
    }

    @Test
    fun filter() {
        val liveData: MutableLiveData<Boolean> = MutableLiveData()
        val actuals: MutableList<Boolean?> = mutableListOf()
        val observer: (t: Boolean?) -> Unit = { actuals.add(it) }
        liveData
                .filter { it == true }
                .observe(this, observer)

        liveData.value = true
        liveData.value = false
        liveData.value = false
        liveData.value = true
        liveData.value = null

        val expecteds = mutableListOf(true, true)
        assertEquals(expecteds, actuals)
    }

    @Test
    fun first() {
        val liveData: MutableLiveData<Boolean> = MutableLiveData()
        val actuals: MutableList<Boolean?> = mutableListOf()
        val observer: (t: Boolean?) -> Unit = { actuals.add(it) }
        liveData
                .first()
                .observe(this, observer)

        liveData.value = true
        liveData.value = false
        liveData.value = false
        liveData.value = true

        val expecteds = mutableListOf(true)
        assertEquals(expecteds, actuals)
    }

    @Test
    fun map() {
        val liveData: MutableLiveData<Boolean> = MutableLiveData()
        val actuals: MutableList<Boolean?> = mutableListOf()
        val observer: (t: Boolean?) -> Unit = { actuals.add(it) }
        liveData
                .map { true }
                .observe(this, observer)

        liveData.value = true
        liveData.value = false
        liveData.value = false
        liveData.value = true

        val expecteds = mutableListOf(true, true, true, true)
        assertEquals(expecteds, actuals)
    }

    @Test
    fun nonNull() {
        val liveData: MutableLiveData<Boolean> = MutableLiveData()
        val actuals: MutableList<Boolean> = mutableListOf()
        val observer: (t: Boolean) -> Unit = { actuals.add(it) }
        liveData
                .nonNull()
                .observe(this, observer)

        liveData.value = true
        liveData.value = null
        liveData.value = false
        liveData.value = null
        liveData.value = false
        liveData.value = null
        liveData.value = true
        liveData.value = null

        val expecteds = mutableListOf(true, false, false, true)
        assertEquals(expecteds, actuals)
    }
}