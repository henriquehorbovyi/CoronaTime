package io.henrikhorbovyi.coronamap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.henrikhorbovyi.coronamap.remote.GlobalInfo
import io.henrikhorbovyi.coronamap.usecase.FetchGlobalInfo
import io.henrikhorbovyi.coronamap.usecase.invoke
import kotlinx.coroutines.launch

/**
 * .:.:.:. Created by @henrikhorbovyi on 4/1/20 .:.:.:.
 */
class GlobalInfoViewModel(
    private val fetchGlobalInfo: FetchGlobalInfo
) : ViewModel() {

    private val globalInfo: MutableLiveData<Result<List<GlobalInfo>>> = MutableLiveData()
    val globalInfoObservable: LiveData<Result<List<GlobalInfo>>>
        get() = globalInfo

    init {
        viewModelScope.launch {
            val result = fetchGlobalInfo.receiver.receive()
            globalInfo.postValue(result)
        }
    }

    fun fetchInfo() {
        fetchGlobalInfo()
    }

    override fun onCleared() {
        fetchGlobalInfo.close()
        super.onCleared()
    }
}