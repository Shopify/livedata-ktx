[![GitHub license](https://img.shields.io/badge/license-MIT-lightgrey.svg?maxAge=2592000)](https://raw.githubusercontent.com/shopify/livedata-ktx/master/LICENSE)
[ ![Download](https://api.bintray.com/packages/shopify/shopify-android/livedata-ktx/images/download.svg?version=3.0.0) ](https://bintray.com/shopify/shopify-android/livedata-ktx/3.0.0/link)
[![Build Status](https://travis-ci.org/Shopify/livedata-ktx.svg?branch=master)](https://travis-ci.org/Shopify/livedata-ktx)

# livedata-ktx

Kotlin extension for LiveData. This library focuses on three things:

- Kotlin friendly.
- Preserve immutability.
- Extensibility.

About Kotlin friendly, thanks to `androidx.lifecycle:livedata:2.0.0` release, you can explicitly set optinal type for LiveData. LiveData<Boolean> and LiveData<Boolean?> are supported now. However, you can still set null value, for instance:

```kotlin
// Allow to set null value to LiveData
val liveData = MutableLiveData<Boolean>()
liveData.value = null

// LiveDataKtx doesn't allow to null value
val liveData = MutableLiveDataKtx<Boolean>()
liveData.value = null // doesn't allow

// Set nullable in LiveDataKtx
val liveData = MutableLiveDataKtx<Boolean?>()
liveData.value = null
```

[README For 1.x](https://github.com/Shopify/livedata-ktx/blob/master/README.1.x.mdx)

[README For 2.x](https://github.com/Shopify/livedata-ktx/blob/master/README.2.x.mdx)

# Getting Started

To add LiveData KTX to your project, add the following to your app module's build.gradle:

```groovy
implementation "com.shopify:livedata-ktx:VERSION"
```

# Usage

### From LiveData to LiveDataKtx

```kotlin
val liveData = LiveData<Boolean>()
val liveDataKtx = liveData.toKtx()
val nullableLiveDataKtx = liveData.toNullableKtx()
val anotherNullableLiveDataKtx = LiveDataKtx<Boolean?>()

val mutableLiveData = MutableLiveData<Boolean>()
val mutableLiveDataKtx = mutableLiveData.toKtx()
val nullableMutableLiveDataKtx = mutableLiveData.toNullableKtx()
val anotherNullableMutableLiveDataKtx = MutableLiveDataKtx<Boolean?>()

val mediatorLiveData = MediatorLiveData<Boolean>()
val mediatorLiveDataKtx = mediatorLiveData.toKtx()
val nullableMediatorLiveDataKtx = mediatorLiveData.toNullableKtx()
val anotherNullableMediatorLiveDataKtx = MediatorLiveDataKtx<Boolean?>()
```

### Chaining LiveData

```kotlin
val liveData = MutableLiveDataKtx<Boolean>()
liveData
  .filter { it == false }
  .map { true }
  .observe(lifecycleOwner, Observer { result ->
    // result is non-null and always true
  })
```

### PublishLiveData (SingleLiveData in version 2.x)

It is a lifecycle-aware observable that sends only new updates after subscription, used for events like navigation and Snackbar messages. `livedata-ktx` has different implementation comparing to SingleLiveEvent from [google samples android-architecture](https://github.com/googlesamples/android-architecture/blob/dev-todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/SingleLiveEvent.java).

```kotlin
val liveData = PublishLiveDataKtx<Int>()
val actual = mutableListOf<Int>()
val observer = Observer<Int> { actual.add(it) }
liveData.value = -1

liveData.observeForever(observer)
liveData.value = 0
liveData.removeObserver(observer)

assertEquals(listOf(0), actual)
actual.clear()

liveData.value = 1
liveData.value = 2
liveData.observeForever(observer)
liveData.value = 3

assertEquals(listOf(3), actual)
```

For more use cases, please see the tests at [LiveDataKtxTest.kt](https://github.com/shopify/livedata-ktx/blob/master/livedata-ktx/src/test/java/com/shopify/livedataktx/LiveDataKtxTest.kt)

### Use safeValue

LiveDataKtx will throw NPE if you try to get value when it supposes to be nonNull. Use `safeValue` in this case.

```kotlin
val liveDataKtx = MutableLiveDataKtx<Boolean>()

// Throw NPE if the value hasn't been set yet as it is defined as nonNull <Boolean>
liveDataKtx.value

// This works fine as the value has been set
liveDataKtx.value = true
liveDataKtx.value

// safeValue always returns nullable value and does not throw NPE
liveDataKtx.safeValue

// observe doesn't throw NPE even when the value hasn't been set
liveDataKtx.observe(...)
```

# Feel missing methods

It is easy to add your custom extension without requiring to send a PR. For example:

```kotlin
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
```

# Contributing

Any contributions are welcome!
Please check the [CONTRIBUTING](CONTRIBUTING.md) guideline before submitting a new issue. Wanna send PR? [Click HERE](https://github.com/shopify/livedata-ktx/pulls)

# Maintainers

- Francisco Cavedon <[@fcavedon](https://github.com/fcavedon)>
- Henry Tao <[@henrytao-me](https://github.com/henrytao-me)>
- Ivan Savytskyi <[@sav007](https://github.com/sav007)>
- Kris Orr <[@krisorr](https://github.com/krisorr)>

# License

    The MIT License (MIT)

    Copyright (c) 2018, 2019 Shopify Inc.

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
