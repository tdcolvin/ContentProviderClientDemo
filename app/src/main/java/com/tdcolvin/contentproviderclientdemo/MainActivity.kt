package com.tdcolvin.contentproviderclientdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tdcolvin.contentproviderclientdemo.ui.theme.ContentProviderClientDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContentProviderClientDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BirthdaysScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BirthdaysScreen(modifier: Modifier = Modifier, viewModel: BirthdaysClientViewModel = viewModel()) {
    val birthdates by viewModel.birthdates.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LazyColumn(modifier = modifier) {
        item {
            Column {
                Text(
                    text = "Client content resolver app",
                    style = MaterialTheme.typography.displayMedium
                )

                Text("This app gets its data from the ContentProviderDemo app's database, via its exported ContentProvider. Ensure you have installed that app and run it once to set up the database.")
            }
        }
        item {
            Button(onClick = viewModel::loadBirthdates) {
                Text("Load Data From Demo App")
            }
        }
        error?.let { error ->
            item {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
        items(items = birthdates) { item ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .border(border = BorderStroke(width = 2.dp, color = Color.Blue), shape = RoundedCornerShape(10.dp))
                    .padding(10.dp),
                text = "${item.name} born ${item.birthdate}",
                fontSize = 25.sp
            )
        }
        item {
            Button(onClick = viewModel::addNewRandomBirthdate) {
                Text("Add Birthdate")
            }
        }
    }
}