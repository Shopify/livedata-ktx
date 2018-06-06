# livedata-ktx #

### Version 0.1.16 – 06/6/2018 ###

- Fix race condition in SupportMediatorLiveData when calling postValue

### Version 0.1.15 – 05/24/2018 ###

- Replace observeForever extension with observe (without LifecycleOwner) because of naming conflict. 

### Version 0.1.14 – 05/19/2018 ###

Supporting operators: 
- debounce
- distinct
- filter
- first
- map
- nonNull

Supporting LiveData:
- SingleLiveData
