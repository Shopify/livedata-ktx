[![GitHub license](https://img.shields.io/badge/license-MIT-lightgrey.svg?maxAge=2592000)](https://raw.githubusercontent.com/shopify/livedata-ktx/master/LICENSE)
[ ![Download](https://api.bintray.com/packages/shopify/shopify-android/livedata-ktx/images/download.svg) ](https://bintray.com/shopify/shopify-android/livedata-ktx/_latestVersion)
[![Build Status](https://travis-ci.org/Shopify/livedata-ktx.svg?branch=master)](https://travis-ci.org/Shopify/livedata-ktx)

# livedata-ktx
Kotlin extension for LiveData, chaining like RxJava


# Getting Started

To add LiveData KTX to your project, add the following to your app module's build.gradle:

```groovy
implementation "com.shopify:livedata-ktx:VERSION"
```

*(See latest version on top of README)*


# Usage


### Chaining LiveData

```kotlin
val liveData: MutableLiveData<Boolean> = MutableLiveData()
liveData
  .distinct()
  .filter { it == false }
  .map { true }
  .nonNull()
  .observe(lifecycleOwner, { result ->
    // result is non-null and always true
  })
```

### Remove observer

Because the input observer goes through a wrapper before it observes to source LiveData. So that you can't simply remove it by just calling origin method `liveData.removeObserver`.
 
The new observe method returns `Removable` interface that allows you to remove observer effectively.  

```kotlin
val liveData: MutableLiveData<Boolean> = MutableLiveData()
val removable = liveData
  .nonNull()
  .observe(lifecycleOwner, {
    // TODO
  })
removable.removeObserver()
```

### SingleLiveData

It is a lifecycle-aware observable that sends only new updates after subscription, used for events like navigation and Snackbar messages. `livedata-ktx` has different implementation comparing to SingleLiveEvent from [google samples android-architecture](https://github.com/googlesamples/android-architecture/blob/dev-todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/SingleLiveEvent.java).

```kotlin
val liveData: MutableLiveData<Int> = SingleLiveData()
val actuals: MutableList<Int?> = mutableListOf()
val observer: (t: Int?) -> Unit = { actuals.add(it) }

liveData.value = 1
liveData.observe(this, observer)
liveData.value = 2
liveData.value = 3

val expecteds = mutableListOf(2, 3)
assertEquals(expecteds, actuals)
```

For more use cases, please see the tests at [LiveDataTest.kt](https://github.com/shopify/livedata-ktx/blob/master/livedata-ktx/src/test/java/com/shopify/livedataktx/LiveDataTest.kt)


# Feel missing methods

Please suggest what you need by creating issues. I will support it as fast as I can. 


# Contributing

Any contributions are welcome!  
Please check the [CONTRIBUTING](CONTRIBUTING.md) guideline before submitting a new issue. Wanna send PR? [Click HERE](https://github.com/shopify/livedata-ktx/pulls)

# Maintainers

- Henry Tao <[henry.tao@shopify.com](mailto:henry.tao@shopify.com)>
- Ivan Savytskyi <[ivan.savytskyi@shopify.com](mailto:ivan.savytskyi@shopify.com)>


# License

    The MIT License (MIT)

    Copyright (c) 2018 Shopify Inc.

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


