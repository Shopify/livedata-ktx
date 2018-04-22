[ ![Download](https://api.bintray.com/packages/henrytao-me/maven/livedata-ktx/images/download.svg) ](https://bintray.com/henrytao-me/maven/livedata-ktx/_latestVersion)

# livedata-ktx
Kotlin extension for LiveData, chaining like RxJava

# Usage (WIP)

```groovy
implementation "me.henrytao:livedata-ktx:*"
```

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
