package com.example.uasecom

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uasecom.data.GoogleAuthUIClient
import com.example.uasecom.presentation.MainAppScreen
import com.example.uasecom.presentation.home.HomeScreen
import com.example.uasecom.presentation.profile.ProfileScreen
import com.example.uasecom.presentation.sign_in.SignInScreen
import com.example.uasecom.presentation.sign_in.SignInViewModel
import com.example.uasecom.presentation.splash.SplashScreen
import com.example.uasecom.ui.theme.UASEcomTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = this
        )
    }
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UASEcomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "sign_in") {
                        composable("splash") {
                            SplashScreen (onNavigateToNext = {
                                navController.navigate("sign_in") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }
                        composable("sign_in") {
                            val viewModel = SignInViewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            // UBAH TUJUAN: Jika sudah login, ke "home"
                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUIClient.getSignedInUser() != null) {
                                    navController.navigate("home") {
                                        popUpTo("sign_in") { inclusive = true } // Agar tidak bisa back ke login
                                    }
                                }
                            }

                            // UBAH TUJUAN: Jika sukses login, ke "home"
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home") {
                                        popUpTo("sign_in") { inclusive = true }
                                    }
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val result = googleAuthUIClient.signIn()
                                        viewModel.onSignInResult(result)
                                    }
                                }
                            )
                        }

                        composable("home") {
                            MainAppScreen(
                                userData = googleAuthUIClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUIClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Kembali ke login dan hapus stack navigasi
                                        navController.navigate("sign_in") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

