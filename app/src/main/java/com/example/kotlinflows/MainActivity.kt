package com.example.kotlinflows

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinflows.ui.theme.KotlinFlowsTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* COMPOSE */
        setContent {
            val viewModel = viewModel<MainViewModel>()
//            val time = viewModel.countdownFlow.collectAsState(initial = 10)
            val count = viewModel.stateFlow.collectAsState(initial = 0)
//            viewModel.stateFlow.value = 10 // The UI can not change directly the value of a flow
            KotlinFlowsTheme {
                Box(modifier = Modifier.fillMaxSize()) {
//                    Text(
//                        text = time.value.toString(),
//                        fontSize = 30.sp,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
                    Button(onClick = { viewModel.incrementCounter() }) {
                        Text(text = "Counter: ${count.value}")
                    }
                }
            }
        }
        /* XML */
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.stateFlow.collectLatest { number ->
//                    binding.tvCounter.text = number.toString()
//                }
//            }
//        }
        collectLatestLifecycleFlow(viewModel.stateFlow) { number ->
            binding.tvCounter.text = number.toString()
        }
    }
}

// Utility extension function
// AppCompactActivity for XML
fun <T> ComponentActivity.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }

}