[ ![Download](https://api.bintray.com/packages/henrytao-me/maven/livedata-ktx/images/download.svg) ](https://bintray.com/henrytao-me/maven/livedata-ktx/_latestVersion)

# livedata-ktx
Kotlin extension for LiveData, chaining like RxJava


# Getting Started

To add LiveData KTX to your project, add the following to your app module's build.gradle:

```groovy
implementation "me.henrytao:livedata-ktx:VERSION"
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

# Feel missing methods

Please suggest what you need by creating issues. I will support it as fast as I can. 


# Contributing

Any contributions are welcome!  
Please check the [CONTRIBUTING](CONTRIBUTING.md) guideline before submitting a new issue. Wanna send PR? [Click HERE](https://github.com/henrytao-me/livedata-ktx/pulls)


# License

    Copyright (c) 2018 Henry Tao <hi@henrytao.me>.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


