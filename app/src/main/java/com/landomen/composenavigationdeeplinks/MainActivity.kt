package com.landomen.composenavigationdeeplinks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.landomen.composenavigationdeeplinks.ui.theme.ComposeNavigationDeeplinksTheme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val DEEPLINK_BASE = "https://deeplink.sample.com"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeNavigationDeeplinksTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Destinations.Input,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<Destinations.Input> {
                        InputScreen(onNextScreenClick = { firstName, lastName, age ->
                            navController.navigate(Destinations.Result(firstName, lastName, age))
                        })
                    }
                    composable<Destinations.Result>(
                        deepLinks = listOf(
                            navDeepLink<Destinations.Result>(basePath = "${DEEPLINK_BASE}/result")
                        )
                    ) { backStackEntry ->
                        val backStackEntryRoute = backStackEntry.toRoute<Destinations.Result>()
                        ResultScreen(
                            firstName = backStackEntryRoute.firstName,
                            lastName = backStackEntryRoute.lastName,
                            age = backStackEntryRoute.age,
                            onPreviousScreenClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputScreen(
    onNextScreenClick: (
        firstName: String,
        lastName: String,
        age: Int,
    ) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Input Screen", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(text = "First name") }
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(text = "Last name") }
        )

        TextField(
            value = age.toString(),
            onValueChange = { age = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            label = { Text(text = "Age") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNextScreenClick(firstName, lastName, age.toIntOrNull() ?: 0) }) {
            Text(text = "Submit")
        }
    }
}

@Composable
private fun ResultScreen(
    firstName: String?,
    lastName: String?,
    age: Int?,
    onPreviousScreenClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Result Screen", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "Received first name: ")
            Text(
                text = firstName.toString(),
                color = if (firstName.isNullOrEmpty()) Color.Red else Color.Green
            )
        }
        Row {
            Text(text = "Received last name: ")
            Text(
                text = lastName.toString(),
                color = if (lastName.isNullOrEmpty()) Color.Red else Color.Green
            )
        }
        Row {
            Text(text = "Received age: ")
            Text(
                text = age.toString(),
                color = if (age == null) Color.Red else Color.Green
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onPreviousScreenClick) {
            Text(text = "Back to input")
        }
    }
}

sealed class Destinations {

    @Serializable
    data object Input : Destinations()

    @Serializable
    data class Result(
        @SerialName("lastName")
        val lastName: String,
        @SerialName("firstName")
        val firstName: String,
        // need to be optional or have a default value to be an argument and not a path parameter
        val age: Int = 0,
    ) : Destinations()
}