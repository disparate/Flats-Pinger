package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kazarovets.flatspinger.views.ContentViewState


class LoadingStateData {
    private val mutableStateData = MutableLiveData<ContentViewState>()
    val stateData: LiveData<ContentViewState> = mutableStateData

    private val mutableIsRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = mutableIsRefreshing

    private val mutableIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mutableIsLoading

    fun setState(state: ContentViewState) {
        mutableStateData.postValue(state)
    }

    fun setIsRefreshing(isLoading: Boolean) {
        mutableIsRefreshing.postValue(isLoading)
    }

    fun setIsLoading(isLoading: Boolean) {
        mutableIsLoading.postValue(isLoading)
    }
}