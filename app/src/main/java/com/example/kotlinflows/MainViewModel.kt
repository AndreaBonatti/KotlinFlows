package com.example.kotlinflows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val dispatcher: DispatcherProvider
) : ViewModel() {

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
    }.flowOn(dispatcher.main) // to set flow on a chosen dispatcher
    // up here there is a cold flow
    // cold flow === no collector => do anything
    // hot flow === do something even if there are no collectors

    // StateFlow = follow tha contains a state with a single value
    // like liveData without the lifecycle awareness
    // (flow can not detect when activity goes in the background)
    // it is an hot flow
    private val _stateFlow =
        MutableStateFlow(0) // mutable version tha only the viewModel can modify
    val stateFlow = _stateFlow.asStateFlow() // immutable version for the UI

    // SharedFlow is used to send one-time event e.g. log-in event, navigation event, ...
    // StateFlow re-emit the same event if the screen is rotated, SharedFlow no
    // SharedFlow is a cold flow => no collector === event sent lost
    private val _sharedFlow =
        MutableSharedFlow<Int>() // mutable version tha only the viewModel can modify

    // The sharedFlow can cache x emissions of the same  flow by using the replay parameter
//    private val _sharedFlow =
//        MutableSharedFlow<Int>(replay = 5) // 5 emissions cached
    val sharedFlow = _sharedFlow.asSharedFlow() // immutable version for the UI

    init {
//        collectFlow()
//        squareNumber(3) here the shared flow not works because there is no collector
        // with replay version can works even here
        viewModelScope.launch(dispatcher.main) {
            sharedFlow.collect {
                delay(2000L)
                println("1ST FLOW: The received number is: $it")
            }
        }
        viewModelScope.launch(dispatcher.main) {
            sharedFlow.collect {
                delay(3000L)
                println("2ND FLOW: The received number is: $it")
            }
        }
        squareNumber(3)
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch(dispatcher.main) {
            _sharedFlow.emit(number * number)
        }
    }

    fun incrementCounter() {
        _stateFlow.value += 1
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