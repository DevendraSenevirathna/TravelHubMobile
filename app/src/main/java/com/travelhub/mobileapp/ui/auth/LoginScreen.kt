package com.travelhub.mobileapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.clickableText
import com.travelhub.mobileapp.data.model.AuthResult
import com.travelhub.mobileapp.ui.AppViewModelFactory

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(factory = AppViewModelFactory(context))
    val authResult by viewModel.authResult.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authResult) {
        if (authResult is AuthResult.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome Back 🌿", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Sign in to continue your travel journey",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (authResult is AuthResult.Error) {
            Text(
                (authResult as AuthResult.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(username, password) },
            enabled = authResult !is AuthResult.Loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (authResult is AuthResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        TextButton(
            onClick = { /* TODO: forgot password flow, not in current API scope */ },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
        ) {
            Text("Forgot Password?")
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Create new account",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickableText { onNavigateToRegister() }
            )
        }
    }
}