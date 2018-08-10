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
import androidx.lifecycle.Observer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LiveDataKtxTest : LifecycleOwner {

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
    fun nonNull() {
        val liveData = MutableLiveDataKtx<Boolean>()
        val observeValues = mutableListOf<Boolean>()

        liveData.observe(this, Observer {
            observeValues.add(it)
        })

        liveData.value = true
        liveData.value = false

        val expected: Boolean = liveData.value
        assertEquals(expected, false)
        assertEquals(observeValues, listOf(true, false))
    }

    @Test
    fun nullable() {
        val liveData = MutableLiveDataKtx<Boolean?>()
        val observeValues = mutableListOf<Boolean?>()

        liveData.observe(this, Observer {
            observeValues.add(it)
        })

        liveData.value = null
        liveData.value = true
        liveData.value = null
        liveData.value = false

        val expected: Boolean? = liveData.value
        assertEquals(expected, false)
        assertEquals(observeValues, listOf(null, true, null, false))
    }

    @Test
    fun filter() {
        val liveData = MutableLiveDataKtx<Boolean>()
        val actual = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { actual.add(it) }
        liveData
            .filter { it == true }
            .observe(this, observer)

        liveData.value = true
        liveData.value = false
        liveData.value = true
        liveData.value = false

        val expected = mutableListOf(true, true)
        assertEquals(expected, actual)
    }

    @Test
    fun filter_nullable() {
        val liveData = MutableLiveDataKtx<Boolean?>()
        val actual = mutableListOf<Boolean?>()
        val observer = Observer<Boolean?> { actual.add(it) }
        liveData
            .filter { it == true }
            .observe(this, observer)

        liveData.value = true
        liveData.value = null
        liveData.value = false
        liveData.value = null
        liveData.value = true
        liveData.value = null
        liveData.value = false

        val expected = mutableListOf(true, true)
        assertEquals(expected, actual)
    }

    @Test
    fun map() {
        val liveData = MutableLiveDataKtx<Boolean>()
        val actual = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { actual.add(it) }
        liveData
            .map { true }
            .observe(this, observer)

        liveData.value = true
        liveData.value = false
        liveData.value = false
        liveData.value = true

        val expected = mutableListOf(true, true, true, true)
        assertEquals(expected, actual)
    }

    @Test
    fun map_nullable() {
        val liveData = MutableLiveDataKtx<Boolean?>()
        val actual = mutableListOf<Boolean?>()
        val observer = Observer<Boolean?> { actual.add(it) }
        liveData
            .map { true }
            .observe(this, observer)

        liveData.value = null
        liveData.value = true
        liveData.value = null
        liveData.value = false
        liveData.value = null
        liveData.value = false
        liveData.value = null
        liveData.value = true

        val expected = mutableListOf(true, true, true, true, true, true, true, true)
        assertEquals(expected, actual)
    }

    @Test
    fun switchMap() {
        val liveData = MutableLiveDataKtx<Int>()
        val actual = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { actual.add(it) }
        val buildSwitchMap = { value: Int ->
            val output = MutableLiveDataKtx<Boolean>()
            output.value = value % 2 == 0
            output
        }
        liveData
            .switchMap { buildSwitchMap(it) }
            .observe(this, observer)

        liveData.value = 0
        liveData.value = 1
        liveData.value = 2
        liveData.value = 3

        val expected = mutableListOf(true, false, true, false)
        assertEquals(expected, actual)
    }

    @Test
    fun switchMap_nullable() {
        val liveData = MutableLiveDataKtx<Int?>()
        val actual = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { actual.add(it) }
        val buildSwitchMap = { value: Int? ->
            val output = MutableLiveDataKtx<Boolean>()
            output.value = value?.let { it % 2 == 0 } ?: false
            output
        }
        liveData
            .switchMap { buildSwitchMap(it) }
            .observe(this, observer)

        liveData.value = null
        liveData.value = 0
        liveData.value = null
        liveData.value = 1
        liveData.value = null
        liveData.value = 2
        liveData.value = null
        liveData.value = 3

        val expected = mutableListOf(false, true, false, false, false, true, false, false)
        assertEquals(expected, actual)
    }

    @Test
    fun toKtx() {
        val liveData = MutableLiveData<Boolean>()
        val actual = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { actual.add(it) }
        val liveDataKtx: MutableLiveDataKtx<Boolean> = liveData.toKtx()
        liveDataKtx.observe(this, observer)

        liveData.value = null
        liveData.value = true
        liveData.value = null
        liveData.value = false
        liveData.value = null
        liveData.value = true
        liveData.value = null
        liveData.value = false

        val expected = mutableListOf(true, false, true, false)
        assertEquals(expected, actual)
    }

    @Test
    fun toKtx_nullable() {
        val liveData = MutableLiveData<Boolean?>()
        val actual = mutableListOf<Boolean?>()
        val observer = Observer<Boolean?> { actual.add(it) }
        val liveDataKtx: MutableLiveDataKtx<Boolean?> = liveData.toNullableKtx()
        liveDataKtx.observe(this, observer)

        liveData.value = null
        liveData.value = true
        liveData.value = null
        liveData.value = false
        liveData.value = null
        liveData.value = true
        liveData.value = null
        liveData.value = false

        val expected = mutableListOf(null, true, null, false, null, true, null, false)
        assertEquals(expected, actual)
    }

    @Test
    fun ktx_reattach() {
        val liveData = MutableLiveData<Int>()
        val actual = mutableListOf<Int>()
        val observer = Observer<Int> { actual.add(it) }
        val liveDataKtx: MutableLiveDataKtx<Int> = liveData.toKtx()
        liveDataKtx.observe(this, observer)

        liveData.value = 0
        liveDataKtx.removeObserver(observer)
        liveData.value = 1
        liveData.value = 2
        liveDataKtx.observe(this, observer)
        liveData.value = 3

        val expected = mutableListOf(0, 2, 3)
        assertEquals(expected, actual)
    }

    @Test
    fun publish_reattach() {
        val liveData = PublishLiveDataKtx<Boolean>()
        val actual = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { actual.add(it) }
        liveData.observe(this, observer)

        liveData.value = true
        liveData.removeObserver(observer)
        liveData.value = false
        liveData.value = true
        liveData.observe(this, observer)
        liveData.value = false

        val expected = mutableListOf(true, false)
        assertEquals(expected, actual)
    }
}