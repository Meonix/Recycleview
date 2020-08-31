package com.mionix.myapplication.di

import com.mionix.myapplication.DBViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
        viewModel { DBViewModel(get()) }
//    viewModel { InTheatresViewModel(get()) }
//    viewModel { HomeFragmentViewModel(get(),get()) }
//    viewModel { MovileDetailViewModel(get(),get()) }
//    viewModel { LoginViewModel() }
//    viewModel { RegisterViewModel() }
}