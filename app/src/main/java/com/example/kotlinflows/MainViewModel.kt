package com.example.kotlinflows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.InternalCoroutinesApi
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

    // [[1, 2, 3], [1, 2]]
    // [1, 2, 3, 1, 2] == flattening a list

    private fun collectFlow() {
//            countdownFlow.onEach { time ->
//                println(time)
//            }.launchIn(viewModelScope)

//        val flow1 = flow {
//            emit(1)
//            delay(500L)
//            emit(2)
//        }
//
//        viewModelScope.launch {
//            flow1.flatMapConcat { value ->
//                flow {
//                    emit(value + 1)
//                    delay(500L)
//                    emit(value + 2)
//                }
//            }.collect { value ->
//                println("The value is $value")
//            }
//        }

//            // collect vs collectLatest
//            // if there is a new emission and the previous one is not finished
//            // collectLatest collect only the latest
//            // => a big delay could show us only the latest emission (0)
//            // collectLatest => useful to reflect UI state
//            // where the latest state is the more important
////            val count = countdownFlow
//            val reduceResult = countdownFlow
////                .filter { time ->
////                    time % 2 == 0
////                }
////                .map { time ->
////                    time * time
////                }
////                .onEach { time ->
////                    println(time)
////                }
////                .collect { time ->
////                    println("The current time is $time")
////                }
////                .count { time ->
////                    time % 2 == 0
////                }
////                .reduce { accumulator, value ->
////                    accumulator + value
////                }
//                .fold(100) { accumulator, value ->
//                    accumulator + value
//                }
////            println("The count is $count")
//            println("The reduce result is $reduceResult")
//        }

//        val flow1 = (1..5).asFlow()
//        viewModelScope.launch {
//            flow1.flatMapConcat { id ->
//                getRecipeById(id)
//            }.collect { value ->
//                println("The value is $value")
//            }
//        }

        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")
            }
//                .buffer()// The part below runs in a different coroutine
                .conflate()// The part below runs in a different coroutine, go to the latest emissions
                .collect {
                println("FLOW: Now eating $it")
                delay(1500L)
                println("FLOW: Finished eating $it")
            }
        }
    }
}