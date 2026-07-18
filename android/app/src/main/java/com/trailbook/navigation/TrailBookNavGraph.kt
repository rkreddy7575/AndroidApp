package com.trailbook.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trailbook.core.datastore.TokenStorage
import com.trailbook.core.design.theme.TrailBookTheme
import com.trailbook.feature.authentication.ui.LoginScreen
import com.trailbook.feature.authentication.ui.RegisterScreen
import com.trailbook.feature.authentication.ui.SplashScreen
import com.trailbook.feature.experience.ui.CreateExperienceScreen
import com.trailbook.feature.experience.ui.ExperienceDetailScreen
import com.trailbook.feature.explore.ui.ExploreScreen
import com.trailbook.feature.home.ui.HomeScreen
import com.trailbook.feature.profile.ui.BookmarksScreen
import com.trailbook.feature.profile.ui.ProfileScreen
import com.trailbook.feature.profile.ui.SettingsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Main : Route("main")
    data object Home : Route("home")
    data object Explore : Route("explore")
    data object Create : Route("create")
    data object Bookmarks : Route("bookmarks")
    data object Profile : Route("profile")
    data object Settings : Route("settings")
    data object ExperienceDetail : Route("experience/{experienceId}") {
        fun create(id: String) = "experience/$id"
    }
    data object EditExperience : Route("edit/{experienceId}") {
        fun create(id: String) = "edit/$id"
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

@HiltViewModel
class ThemeViewModel @Inject constructor(tokenStorage: TokenStorage) : androidx.lifecycle.ViewModel() {
    val themeMode = tokenStorage.themeMode
}

@Composable
fun TrailBookAppRoot(themeViewModel: ThemeViewModel = hiltViewModel()) {
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle(initialValue = "system")
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    TrailBookTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Route.Splash.route) {
            composable(Route.Splash.route) {
                SplashScreen(
                    onAuthenticated = {
                        navController.navigate(Route.Main.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    },
                    onUnauthenticated = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Route.Main.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Route.Register.route) }
                )
            }
            composable(Route.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Route.Main.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable(Route.Main.route) {
                MainScreen(
                    onExperienceClick = { id ->
                        navController.navigate(Route.ExperienceDetail.create(id))
                    },
                    onCreateExperience = { navController.navigate(Route.Create.route) },
                    onLogout = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Main.route) { inclusive = true }
                        }
                    },
                    onNavigateToSettings = { navController.navigate(Route.Settings.route) }
                )
            }
            composable(
                route = Route.ExperienceDetail.route,
                arguments = listOf(navArgument("experienceId") { type = NavType.StringType })
            ) {
                ExperienceDetailScreen(onEdit = { id ->
                    navController.navigate(Route.EditExperience.create(id))
                })
            }
            composable(Route.Create.route) {
                CreateExperienceScreen(
                    onPublished = { id ->
                        navController.navigate(Route.ExperienceDetail.create(id)) {
                            popUpTo(Route.Main.route)
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(
                route = Route.EditExperience.route,
                arguments = listOf(navArgument("experienceId") { type = NavType.StringType })
            ) {
                CreateExperienceScreen(
                    onPublished = { id ->
                        navController.navigate(Route.ExperienceDetail.create(id)) {
                            popUpTo(Route.Main.route)
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Route.Settings.route) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun MainScreen(
    onExperienceClick: (String) -> Unit,
    onCreateExperience: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem(Route.Home.route, "Home", Icons.Default.Home),
        BottomNavItem(Route.Explore.route, "Explore", Icons.Default.Explore),
        BottomNavItem(Route.Create.route, "Create", Icons.Default.Add),
        BottomNavItem(Route.Bookmarks.route, "Saved", Icons.Default.Bookmark),
        BottomNavItem(Route.Profile.route, "Profile", Icons.Default.Person)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (item.route == Route.Create.route) {
                                onCreateExperience()
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Home.route) {
                HomeScreen(onExperienceClick = onExperienceClick)
            }
            composable(Route.Explore.route) {
                ExploreScreen(onExperienceClick = onExperienceClick)
            }
            composable(Route.Bookmarks.route) {
                BookmarksScreen(onExperienceClick = onExperienceClick)
            }
            composable(Route.Profile.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    onExperienceClick = onExperienceClick,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}
