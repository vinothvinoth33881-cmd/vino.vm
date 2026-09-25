package com.example.ui.navigation

enum class KidneyAiScreen(val title: String, val isProtected: Boolean) {
    LANDING("Kidney AI Platform", false),
    LOGIN("Researcher Sign In", false),
    REGISTER("Account Registration", false),
    FORGOT_PASSWORD("Password Recovery", false),
    DASHBOARD("Research Dashboard", true),
    NEW_ANALYSIS("Kidney Image Analysis", true),
    ANALYSIS_RESULT("Diagnosis Results", true),
    HISTORY("Analysis History", true),
    REPORTS("Clinical Decision Reports", true),
    MODEL_INFORMATION("Model & Architecture Specs", true),
    PROFILE("Researcher Profile", true),
    SETTINGS("System & Model Settings", true);

    companion object {
        val drawerDestinations = listOf(
            DASHBOARD,
            NEW_ANALYSIS,
            HISTORY,
            REPORTS,
            MODEL_INFORMATION,
            PROFILE,
            SETTINGS
        )
    }
}
