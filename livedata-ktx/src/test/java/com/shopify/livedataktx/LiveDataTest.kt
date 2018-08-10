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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
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

    @Test
    fun single_observeDirectly() {
        val liveData: MutableLiveData<Int> = SingleLiveData()
        val actuals: MutableList<Int?> = mutableListOf()
        val observer: (t: Int?) -> Unit = { actuals.add(it) }

        liveData.value = 1
        liveData.observe(this, observer)
        liveData.value = 2
        liveData.value = 3

        val expecteds = mutableListOf(2, 3)
        assertEquals(expecteds, actuals)
    }

    @Test
    fun single_observeDirectlyMoreThanOneInDifferentTime() {
        val liveData: MutableLiveData<Int> = SingleLiveData()
        val actuals1: MutableList<Int?> = mutableListOf()
        val observer1: (t: Int?) -> Unit = { actuals1.add(it) }
        val actuals2: MutableList<Int?> = mutableListOf()
        val observer2: (t: Int?) -> Unit = { actuals2.add(it) }

        liveData.value = 1
        liveData.observe(this, observer1)
        liveData.value = 2
        liveData.observe(this, observer2)
        liveData.value = 3
        liveData.value = 4

        val expecteds1 = mutableListOf(2, 3, 4)
        val expecteds2 = mutableListOf(3, 4)
        assertEquals(expecteds1, actuals1)
        assertEquals(expecteds2, actuals2)
    }

    @Test
    fun single_combineWithOtherExtension() {
        val liveData: MutableLiveData<Int> = SingleLiveData()
        val actuals1: MutableList<Int?> = mutableListOf()
        val observer1: (t: Int?) -> Unit = { actuals1.add(it) }
        val actuals2: MutableList<Int?> = mutableListOf()
        val observer2: (t: Int?) -> Unit = { actuals2.add(it) }

        liveData.value = 1
        liveData
                .map { it?.let { it + 1 } }
                .observe(this, observer1)
        liveData.value = 2
        liveData
                .nonNull()
                .map { it + 2 }
                .observe(this, observer2)
        liveData.value = 3
        liveData.value = 4

        val expecteds1 = mutableListOf(3, 4, 5)
        val expecteds2 = mutableListOf(5, 6)
        assertEquals(expecteds1, actuals1)
        assertEquals(expecteds2, actuals2)
    }

    @Test
    fun combineWith() {
        val firstSource = MutableLiveData<Int>()
        val secondSource = MutableLiveData<String>()
        val actuals = mutableListOf<Int?>()
        val observer: (t: Int?) -> Unit = { actuals.add(it) }

        firstSource.value = 1
        secondSource.value = "2"

        firstSource
                .combineWith(secondSource) { i, s -> i!! + s!!.toInt() }
                .observe(this, observer)

        observer(1)

        firstSource
                .nonNull()
                .combineWith(secondSource) { i, s -> i + s!!.toInt() }
                .observe(this, observer)

        observer(1)

        firstSource
                .combineWith(secondSource.nonNull()) { i, s -> i!! + s.toInt() }
                .observe(this, observer)

        observer(1)

        firstSource
                .nonNull()
                .combineWith(secondSource.nonNull()) { i, s -> i + s.toInt() }
                .observe(this, observer)

        assertEquals(mutableListOf(3, 1, 3, 1, 3, 1, 3), actuals)
    }

    @Test
    fun removeObserver(){
        val liveData: MutableLiveData<Boolean> = MutableLiveData()
        val actuals: MutableList<Boolean?> = mutableListOf()
        val observer: (t: Boolean?) -> Unit = { actuals.add(it) }
        val removable = liveData
                .observe(this, observer)

        liveData.value = true
        liveData.value = false
        removable.removeObserver()
        liveData.value = true

        val expecteds = mutableListOf(true, false)
        assertEquals(expecteds, actuals)
    }
}