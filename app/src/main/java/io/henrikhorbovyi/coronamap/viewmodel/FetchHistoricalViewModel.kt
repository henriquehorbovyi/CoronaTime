package io.henrikhorbovyi.coronamap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.henrikhorbovyi.coronamap.remote.Historical
import io.henrikhorbovyi.coronamap.usecase.FetchHistoricalUseCase
import io.henrikhorbovyi.coronamap.usecase.invoke
import kotlinx.coroutines.launch

/**
 * .:.:.:. Created by @henrikhorbovyi on 4/2/20 .:.:.:.
 */
class FetchHistoricalViewModel(
    private val fetchHistoricalUseCase: FetchHistoricalUseCase
) : ViewModel() {

    private val historical: MutableLiveData<Result<Historical>> = MutableLiveData()
    val historicalObservable: LiveData<Result<Historical>>
        get() = historical

    init {
        fetchHistoricalUseCase()
        viewModelScope.launch {
            val result: Result<Historical> = fetchHistoricalUseCase.receiver.receive()
            historical.postValue(result)
        }
    }

    override fun onCleared() {
        fetchHistoricalUseCase.close()
        super.onCleared()
    }
}