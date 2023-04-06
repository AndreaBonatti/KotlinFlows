package com.example.kotlinflows

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Flow = coroutine that can emit multiple values over a period of time
    val countdownFlow = flow<Int> {
//        val staringValue = 10
        val staringValue = 5
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
//            countdownFlow.onEach { time ->
//                println(time)
//            }.launchIn(viewModelScope)

        viewModelScope.launch {

            // collect vs collectLatest
            // if there is a new emission and the previous one is not finished
            // collectLatest collect only the latest
            // => a big delay could show us only the latest emission (0)
            // collectLatest => useful to reflect UI state
            // where the latest state is the more important
//            val count = countdownFlow
            val reduceResult = countdownFlow
//                .filter { time ->
//                    time % 2 == 0
//                }
//                .map { time ->
//                    time * time
//                }
//                .onEach { time ->
//                    println(time)
//                }
//                .collect { time ->
//                    println("The current time is $time")
//                }
//                .count { time ->
//                    time % 2 == 0
//                }
//                .reduce { accumulator, value ->
//                    accumulator + value
//                }
                .fold(100) { accumulator, value ->
                    accumulator + value
                }
//            println("The count is $count")
            println("The reduce result is $reduceResult")
        }
    }
}