package io.henrikhorbovyi.coronamap.di

import io.henrikhorbovyi.coronamap.BuildConfig
import io.henrikhorbovyi.coronamap.remote.CoronaService
import io.henrikhorbovyi.coronamap.remote.CoronaServiceV2
import io.henrikhorbovyi.coronamap.remote.ServiceBuilder
import io.henrikhorbovyi.coronamap.repository.CoronaRepository
import io.henrikhorbovyi.coronamap.repository.RealCoronaRepository
import io.henrikhorbovyi.coronamap.usecase.FetchGlobalInfo
import io.henrikhorbovyi.coronamap.usecase.FetchGlobalStatus
import io.henrikhorbovyi.coronamap.usecase.FetchHistoricalUseCase
import io.henrikhorbovyi.coronamap.viewmodel.FetchHistoricalViewModel
import io.henrikhorbovyi.coronamap.viewmodel.GlobalInfoViewModel
import io.henrikhorbovyi.coronamap.viewmodel.GlobalStatusViewModel
import io.henrikhorbovyi.coronamap.viewmodel.MapCameraUpdateViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */

val appModules = module {

    /* VIEW MODELS */
    viewModel { MapCameraUpdateViewModel() }
    viewModel { GlobalStatusViewModel(fetchGlobalStatus = get()) }
    viewModel { GlobalInfoViewModel(fetchGlobalInfo = get()) }
    viewModel { FetchHistoricalViewModel(fetchHistoricalUseCase = get()) }

    /* USE CASES */
    factory { FetchGlobalStatus(coronaRepository = get()) }
    factory { FetchGlobalInfo(coronaRepository = get()) }
    factory { FetchHistoricalUseCase(coronaRepository = get()) }

    /* REPOSITORIES */
    factory<CoronaRepository> {
        RealCoronaRepository(
            coronaService = get(),
            coronaServiceV2 = get()
        )
    }

    /* SERVICES */
    single { ServiceBuilder<CoronaService>(BuildConfig.CORONA_API) }
    single { ServiceBuilder<CoronaServiceV2>(BuildConfig.CORONA_API_V2) }
}
