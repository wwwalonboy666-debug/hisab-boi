package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.HisabBoiDatabase
import com.example.data.repository.HisabBoiRepository
import com.example.ui.MainApp
import com.example.viewmodel.HisabBoiViewModel
import com.example.viewmodel.HisabBoiViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = HisabBoiDatabase.getInstance(applicationContext)
        val repository = HisabBoiRepository(database)
        val viewModelFactory = HisabBoiViewModelFactory(repository, applicationContext)

        setContent {
            val viewModel: HisabBoiViewModel = viewModel(factory = viewModelFactory)
            MainApp(viewModel = viewModel)
        }
    }
}
