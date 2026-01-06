package com.example.auth.presentation.sign_in

data class SignInState(
    val isSignInSuccessfull:Boolean = false,
    val signInError: String? = null
)