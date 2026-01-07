package com.example.tugascarddigitalid

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tugascarddigitalid.data.GoogleAuthUIClient
import com.example.tugascarddigitalid.presentation.profile.ProfileScreen
import com.example.tugascarddigitalid.presentation.sign_in.SignInScreen
import com.example.tugascarddigitalid.presentation.sign_in.SignInViewModel
import com.example.tugascarddigitalid.ui.theme.TugasCardDigitalIDTheme
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
            TugasCardDigitalIDTheme(){
               Surface(
                   modifier = Modifier.fillMaxSize(),
                   color = MaterialTheme.colorScheme.background
               ) {
                   val navController = rememberNavController()

                   NavHost( navController = navController, startDestination = "sign_in"){
                       composable("sign_in"){
                           val viewModel = SignInViewModel()
                           val state by viewModel.state.collectAsStateWithLifecycle()

                           LaunchedEffect(key1 = Unit) {
                               if (googleAuthUIClient.getSignedInUser() != null) {
                                   navController.navigate("profile")
                               }
                           }
                           LaunchedEffect(key1 = state.isSignInSuccessfull) {
                               if (state.isSignInSuccessfull){
                                   Toast.makeText(
                                       applicationContext,
                                       "Sign in successful",
                                       Toast.LENGTH_LONG
                                   ).show()

                                   navController.navigate("profile")
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
                       composable("profile") {
                           ProfileScreen(
                               userData = googleAuthUIClient.getSignedInUser(),
                               onSignOut = {
                                   lifecycleScope.launch {
                                       googleAuthUIClient.signOut()
                                       Toast.makeText(
                                           applicationContext,
                                           "Signed out",
                                           Toast.LENGTH_LONG
                                       ).show()
                                       navController.popBackStack()
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

