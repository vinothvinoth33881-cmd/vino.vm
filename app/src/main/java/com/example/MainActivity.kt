package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.ui.components.AppHeaderBar
import com.example.ui.components.AppNavigationDrawer
import com.example.ui.navigation.KidneyAiScreen
import com.example.ui.screens.AnalysisResultScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ModelInformationScreen
import com.example.ui.screens.NewAnalysisScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.KidneyAiTheme
import com.example.ui.viewmodel.KidneyAiViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: KidneyAiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            KidneyAiTheme(darkTheme = isDarkTheme) {
                KidneyAiApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KidneyAiApp(viewModel: KidneyAiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDevMode by viewModel.isDevMode.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val analyses by viewModel.userAnalyses.collectAsState()
    val metadata by viewModel.modelMetadata.collectAsState()
    val activeAnalysis by viewModel.currentAnalysis.collectAsState()
    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val selectedImageName by viewModel.selectedImageName.collectAsState()
    val selectedFileSize by viewModel.selectedFileSize.collectAsState()
    val selectedDimensions by viewModel.selectedDimensions.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisError by viewModel.analysisErrorMessage.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Back handling
    BackHandler(enabled = currentScreen != KidneyAiScreen.LANDING) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            val handled = viewModel.handleBack()
            if (!handled && currentScreen != KidneyAiScreen.DASHBOARD && currentUser != null) {
                viewModel.navigateTo(KidneyAiScreen.DASHBOARD)
            }
        }
    }

    val isTopLevelScreen = currentScreen == KidneyAiScreen.LANDING ||
            currentScreen == KidneyAiScreen.LOGIN ||
            currentScreen == KidneyAiScreen.REGISTER ||
            currentScreen == KidneyAiScreen.FORGOT_PASSWORD

    if (isTopLevelScreen) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                KidneyAiScreen.LANDING -> LandingScreen(
                    onNavigate = { viewModel.navigateTo(it) },
                    isAuthenticated = currentUser != null
                )
                KidneyAiScreen.LOGIN -> LoginScreen(
                    onLogin = { email, pass, onResult -> viewModel.login(email, pass, onResult) },
                    onNavigate = { viewModel.navigateTo(it) }
                )
                KidneyAiScreen.REGISTER -> RegisterScreen(
                    onRegister = { name, email, pass, confirm, terms, onResult ->
                        viewModel.register(name, email, pass, confirm, terms, onResult)
                    },
                    onNavigate = { viewModel.navigateTo(it) }
                )
                KidneyAiScreen.FORGOT_PASSWORD -> ForgotPasswordScreen(
                    onRequestReset = { email -> viewModel.requestPasswordReset(email) },
                    onNavigate = { viewModel.navigateTo(it) }
                )
                else -> Unit
            }
        }
    } else {
        // Protected App Shell with Navigation Drawer & Edge-to-Edge Scaffold
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppNavigationDrawer(
                    currentScreen = currentScreen,
                    userFullName = currentUser?.fullName,
                    userEmail = currentUser?.email,
                    isDevMode = isDevMode,
                    onSelectScreen = { dest -> viewModel.navigateTo(dest) },
                    onLogout = { viewModel.logout() },
                    onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    AppHeaderBar(
                        title = currentScreen.title,
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        isDevMode = isDevMode
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        KidneyAiScreen.DASHBOARD -> DashboardScreen(
                            user = currentUser,
                            analyses = analyses,
                            metadata = metadata,
                            isDevMode = isDevMode,
                            onNavigate = { viewModel.navigateTo(it) },
                            onSelectAnalysis = { viewModel.currentAnalysis.value = it }
                        )
                        KidneyAiScreen.NEW_ANALYSIS -> NewAnalysisScreen(
                            bitmap = selectedBitmap,
                            imageName = selectedImageName,
                            fileSize = selectedFileSize,
                            dimensions = selectedDimensions,
                            isAnalyzing = isAnalyzing,
                            errorMessage = analysisError,
                            isDevMode = isDevMode,
                            onPickImage = { uri -> viewModel.setCustomImage(uri) },
                            onSelectBenchmark = { category -> viewModel.setBenchmarkSpecimen(category) },
                            onClearImage = { viewModel.clearSelectedImage() },
                            onStartAnalysis = { viewModel.runAiAnalysis() }
                        )
                        KidneyAiScreen.ANALYSIS_RESULT -> AnalysisResultScreen(
                            analysis = activeAnalysis,
                            bitmap = selectedBitmap,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        KidneyAiScreen.HISTORY -> HistoryScreen(
                            analyses = analyses,
                            onSelectAnalysis = { viewModel.currentAnalysis.value = it },
                            onDeleteAnalysis = { id -> viewModel.deleteAnalysis(id) },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        KidneyAiScreen.REPORTS -> ReportsScreen(
                            analysis = activeAnalysis ?: analyses.firstOrNull(),
                            user = currentUser,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        KidneyAiScreen.MODEL_INFORMATION -> ModelInformationScreen(
                            metadata = metadata
                        )
                        KidneyAiScreen.PROFILE -> ProfileScreen(
                            user = currentUser,
                            analysesCount = analyses.size,
                            onLogout = { viewModel.logout() },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        KidneyAiScreen.SETTINGS -> SettingsScreen(
                            isDevMode = isDevMode,
                            isDarkTheme = isDarkTheme,
                            onToggleDevMode = { viewModel.isDevMode.value = it },
                            onToggleTheme = { viewModel.isDarkTheme.value = it },
                            onChangePassword = { old, new -> viewModel.authRepo.changePassword(old, new) },
                            onLogout = { viewModel.logout() }
                        )
                        else -> Unit
                    }
                }
            }
        }
    }
}
