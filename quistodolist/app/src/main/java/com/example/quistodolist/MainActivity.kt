package com.example.quistodolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quistodolist.data.model.GoogleAuthUIClient
import com.example.quistodolist.presentation.sign_in.SignInScreen
import com.example.quistodolist.presentation.sign_in.SignInViewModel
import com.example.quistodolist.presentation.todo.EditTodoScreen
import com.example.quistodolist.presentation.todo.TodoScreen
import com.example.quistodolist.presentation.todo.TodoViewModel
import com.example.quistodolist.ui.theme.QuistodolistTheme
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(context = this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuistodolistTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val todoViewModel = viewModel<TodoViewModel>()

                    NavHost(
                        navController = navController,
                        startDestination = "sign_in",
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400)
                            ) + fadeIn(animationSpec = tween(400))
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400)
                            ) + fadeOut(animationSpec = tween(400))
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400)
                            ) + fadeIn(animationSpec = tween(400))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400)
                            ) + fadeOut(animationSpec = tween(400))
                        }
                    ) {

                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(Unit) {
                                if (googleAuthUIClient.getSignedInUser() != null) {
                                    navController.navigate("todo_list") { popUpTo("sign_in") { inclusive = true } }
                                }
                            }
                            LaunchedEffect(state.isSignInSuccessfull) {
                                if (state.isSignInSuccessfull) {
                                    navController.navigate("todo_list") { popUpTo("sign_in") { inclusive = true } }
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

                        composable("todo_list") {
                            TodoScreen(
                                userData = googleAuthUIClient.getSignedInUser(),
                                viewModel = todoViewModel,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUIClient.signOut()
                                        navController.navigate("sign_in") {
                                            popUpTo("todo_list") { inclusive = true }
                                        }
                                    }
                                },
                                onNavigateToEdit = { todoId ->
                                    navController.navigate("edit_todo/$todoId")
                                }
                            )
                        }

                        composable(
                            route = "edit_todo/{todoId}",
                            arguments = listOf(navArgument("todoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val todoId = backStackEntry.arguments?.getString("todoId") ?: ""
                            val todos by todoViewModel.todos.collectAsState()
                            val todo = todos.find { it.id == todoId }
                            val userId = googleAuthUIClient.getSignedInUser()?.userId ?: ""

                            todo?.let {
                                EditTodoScreen(
                                    todo = it,
                                    onSave = { newTitle, newPriority ->
                                        todoViewModel.updateTodo(userId, todoId, newTitle, newPriority)
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

