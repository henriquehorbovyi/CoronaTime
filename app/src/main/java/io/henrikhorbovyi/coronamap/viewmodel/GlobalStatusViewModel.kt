package io.henrikhorbovyi.coronamap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.henrikhorbovyi.coronamap.remote.GlobalStatus
import io.henrikhorbovyi.coronamap.usecase.FetchGlobalStatus
import io.henrikhorbovyi.coronamap.usecase.invoke
import kotlinx.coroutines.launch

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */
class GlobalStatusViewModel(
    private val fetchGlobalStatus: FetchGlobalStatus
) : ViewModel() {

    private val globalStatus: MutableLiveData<Result<GlobalStatus>> = MutableLiveData()
    val globalStatusObservable: LiveData<Result<GlobalStatus>>
        get() = globalStatus

    init {
        viewModelScope.launch {
            val result = fetchGlobalStatus.receiver.receive()
            globalStatus.postValue(result)
        }
    }

    fun fetchStatus() {
        fetchGlobalStatus()
    }

    override fun onCleared() {
        fetchGlobalStatus.close()
        super.onCleared()
    }
}
