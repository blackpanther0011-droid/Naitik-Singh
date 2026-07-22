package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.AthleteRepository
import com.example.ui.screens.MainAppScaffold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AthleteViewModel
import com.example.ui.viewmodel.AthleteViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Setup Room Local Database
    val database = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "athlete_reels_database"
    )
    .fallbackToDestructiveMigration()
    .build()

    val appDao = database.appDao()
    val repository = AthleteRepository(appDao)
    val viewModel = ViewModelProvider(
        this,
        AthleteViewModelFactory(repository, applicationContext)
    )[AthleteViewModel::class.java]

    setContent {
      MyApplicationTheme(darkTheme = true) { // Immersive Dark Slate theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
          MainAppScaffold(viewModel = viewModel)
        }
      }
    }
  }
}
