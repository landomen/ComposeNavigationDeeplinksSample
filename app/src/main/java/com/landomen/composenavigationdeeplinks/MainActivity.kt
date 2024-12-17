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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.landomen.composenavigationdeeplinks.ui.theme.ComposeNavigationDeeplinksTheme

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
                    startDestination = Destinations.Input.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(
                        Destinations.Input.route,
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "${DEEPLINK_BASE}/input"
                            },
                        ),
                    ) {
                        InputScreen(onNextScreenClick = { firstName, lastName, age ->
                            navController.navigate(Destinations.Result.route + "/$firstName/$lastName?age=$age")
                        })
                    }
                    composable(
                        route = Destinations.Result.routeWithArguments,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "${DEEPLINK_BASE}/result/{lastName}/{firstName}?age={age}" },
                        ),
                        arguments = listOf(
                            navArgument("firstName"){
                                type = NavType.StringType
                            },
                            navArgument("lastName"){
                                type = NavType.StringType
                            },
                            navArgument("age"){
                                type = NavType.IntType
                            }
                        )
                    ) {
                        ResultScreen(
                            firstName = it.arguments?.getString("firstName"),
                            lastName = it.arguments?.getString("lastName"),
                            age = it.arguments?.getInt("age"),
                            onNextScreenClick = {
                                navController.navigate(Destinations.Third.route)
                            },
                            onPreviousScreenClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(
                        Destinations.Third.route,
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "https://deeplink.sample.com/third"
                            },
                        ),
                    ) {
                        ThirdScreen(
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
    var age by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "First Screen")

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
            onValueChange = { age = it.toIntOrNull() ?: 0 },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            label = { Text(text = "Age") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNextScreenClick(firstName, lastName, age) }) {
            Text(text = "Submit")
        }
    }
}

@Composable
private fun ResultScreen(
    firstName: String?,
    lastName: String?,
    age: Int?,
    onNextScreenClick: () -> Unit,
    onPreviousScreenClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Second Screen")
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

        Row {
            Button(onClick = onPreviousScreenClick) {
                Text(text = "Go to previous screen")
            }
            Button(onClick = onNextScreenClick) {
                Text(text = "Go to next screen")
            }
        }

    }
}

@Composable
private fun ThirdScreen(
    onPreviousScreenClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Third Screen")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onPreviousScreenClick) {
            Text(text = "Go to previous screen")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeNavigationDeeplinksTheme {
        Greeting("Android")
    }
}

sealed class Destinations(val route: String, val routeWithArguments: String) {

    data object Input : Destinations("input", "input")
    data object Result : Destinations("result", "result/{firstName}/{lastName}?age={age}")
    data object Third : Destinations("third", "third")
}