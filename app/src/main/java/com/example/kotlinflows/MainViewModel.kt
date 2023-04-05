package com.example.kotlinflows

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Flow = coroutine that can emit multiple values over a period of time
    val countdownFlow = flow<Int> {
        val staringValue = 10
        var currentValue = staringValue
        emit(staringValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    init {
        collectFlow()
    }

    private fun collectFlow() {
        viewModelScope.launch {
            // collect vs collectLatest
            // if there is a new emission and the previous one is not finished
            // collectLatest collect only the latest
            // => a big delay could show us only the latest emission (0)
            // collectLatest => useful to reflect UI state
            // where the latest state is the more important
            countdownFlow.collectLatest{ time ->
                delay(1500L)
                println("The current time is $time")
            }
        }
    }
}