package com.raharawhey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.raharawhey.ui.Screen
import com.raharawhey.ui.screens.PrayerTimesScreen
import com.raharawhey.ui.screens.QiblaScreen
import com.raharawhey.ui.screens.SettingsScreen
import com.raharawhey.ui.theme.DeepNavy
import com.raharawhey.ui.theme.GoldAccent
import com.raharawhey.ui.theme.GoldDark
import com.raharawhey.ui.theme.RahaRawheyTheme
import com.raharawhey.ui.theme.SurfaceDeep
import com.raharawhey.ui.theme.TextMuted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RahaRawheyTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.mosque_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.5f), blendMode = BlendMode.Darken)
        )
        // Use NavigationRail for the left sidebar layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ── Left Navigation Rail ───────────────────────────────────────────
            NavigationRail(
                modifier = Modifier.fillMaxHeight(),
                containerColor = SurfaceDeep.copy(alpha = 0.8f),
                contentColor   = GoldAccent,
                header = {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "🕌",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            ) {
                Screen.all.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationRailItem(
                        selected = selected,
                        onClick  = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = { Icon(screen.icon, contentDescription = screen.labelAr) },
                        label = {
                            Text(
                                text = screen.labelAr,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor       = GoldAccent,
                            selectedTextColor       = GoldAccent,
                            indicatorColor          = GoldDark.copy(alpha = 0.3f),
                            unselectedIconColor     = TextMuted,
                            unselectedTextColor     = TextMuted
                        )
                    )
                }

                Spacer(Modifier.weight(1f))

                // Decorative gold line at bottom
                Box(
                    Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(Brush.horizontalGradient(listOf(GoldDark, GoldAccent, GoldDark)))
                )
                Spacer(Modifier.height(16.dp))
            }

            // Thin gold separator
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Brush.verticalGradient(listOf(SurfaceDeep, GoldAccent.copy(alpha = 0.3f), SurfaceDeep)))
            )

            // ── Main Content ───────────────────────────────────────────────────
            NavHost(
                navController    = navController,
                startDestination = Screen.PrayerTimes.route,
                modifier         = Modifier.weight(1f),
                enterTransition  = { fadeIn(tween(300)) + slideInHorizontally { it / 5 } },
                exitTransition   = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(300)) },
                popExitTransition  = { fadeOut(tween(200)) + slideOutHorizontally { it / 5 } }
            ) {
                composable(Screen.PrayerTimes.route) { PrayerTimesScreen() }
                composable(Screen.Qibla.route)       { QiblaScreen() }
                composable(Screen.Settings.route)    { SettingsScreen() }
            }
        }
    }
}
