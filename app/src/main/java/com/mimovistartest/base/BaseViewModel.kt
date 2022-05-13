package com.mimovistartest.base

import androidx.lifecycle.*
import com.mimovistartest.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

abstract class BaseViewModel: ViewModel(), LifecycleObserver {
    protected val error by lazy { MutableLiveData<String>() }
    val showErrorEvent: LiveData<Event<String>> = Transformations.map(error) { Event(it) }
}