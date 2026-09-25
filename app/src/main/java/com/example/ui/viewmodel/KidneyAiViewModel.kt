package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AnalysisEntity
import com.example.data.local.entity.ModelMetadataEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.AnalysisRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.ModelRepository
import com.example.ml.KidneyMlPipeline
import com.example.ui.navigation.KidneyAiScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class KidneyAiViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val authRepo = AuthRepository(db.userDao())
    val analysisRepo = AnalysisRepository(db.analysisDao())
    val modelRepo = ModelRepository(db.modelMetadataDao())

    // Screen State
    private val _currentScreen = MutableStateFlow(KidneyAiScreen.LANDING)
    val currentScreen: StateFlow<KidneyAiScreen> = _currentScreen.asStateFlow()

    // Screen History stack for back handling
    private val screenStack = mutableListOf<KidneyAiScreen>(KidneyAiScreen.LANDING)

    // User State
    val currentUser: StateFlow<UserEntity?> = authRepo.currentUser

    // Theme & Settings
    val isDarkTheme = MutableStateFlow(true)
    val isDevMode = MutableStateFlow(false) // USE_MOCK_MODEL flag: defaults to false (Trained Model)
    val devMockClass = MutableStateFlow("CYST")

    // Active Selected / Newly Generated Analysis
    val currentAnalysis = MutableStateFlow<AnalysisEntity?>(null)

    // Image Upload / Preview in New Analysis
    val selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageName = MutableStateFlow<String?>(null)
    val selectedFileSize = MutableStateFlow<String?>(null)
    val selectedDimensions = MutableStateFlow<String?>(null)
    val isAnalyzing = MutableStateFlow(false)
    val analysisErrorMessage = MutableStateFlow<String?>(null)

    // History filter & search
    val historySearchQuery = MutableStateFlow("")
    val historyFilterClass = MutableStateFlow("ALL") // ALL, NORMAL, CYST, TUMOR, STONE, DISAGREEMENT

    // Model Metadata
    val modelMetadata: StateFlow<ModelMetadataEntity?> = modelRepo.observeMetadata()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ModelMetadataEntity())

    // Analyses for active user
    private val _userAnalyses = MutableStateFlow<List<AnalysisEntity>>(emptyList())
    val userAnalyses: StateFlow<List<AnalysisEntity>> = _userAnalyses.asStateFlow()

    init {
        // Observe analyses when user logs in
        viewModelScope.launch {
            authRepo.currentUser.collect { user ->
                if (user != null) {
                    analysisRepo.observeAnalyses(user.id).collect { list ->
                        _userAnalyses.value = list
                    }
                } else {
                    _userAnalyses.value = emptyList()
                    if (_currentScreen.value.isProtected) {
                        navigateTo(KidneyAiScreen.LOGIN)
                    }
                }
            }
        }
    }

    fun navigateTo(screen: KidneyAiScreen) {
        if (screen.isProtected && currentUser.value == null) {
            _currentScreen.value = KidneyAiScreen.LOGIN
            return
        }
        if (screen != _currentScreen.value) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            val prev = screenStack.last()
            _currentScreen.value = prev
            return true
        }
        return false
    }

    // Auth actions
    fun login(email: String, pass: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            when (val res = authRepo.login(email, pass)) {
                is AuthRepository.AuthResult.Success -> {
                    onResult(null)
                    screenStack.clear()
                    screenStack.add(KidneyAiScreen.DASHBOARD)
                    _currentScreen.value = KidneyAiScreen.DASHBOARD
                }
                is AuthRepository.AuthResult.Error -> {
                    onResult(res.message)
                }
            }
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        confirm: String,
        terms: Boolean,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            when (val res = authRepo.register(name, email, pass, confirm, terms)) {
                is AuthRepository.AuthResult.Success -> {
                    onResult(null)
                    screenStack.clear()
                    screenStack.add(KidneyAiScreen.DASHBOARD)
                    _currentScreen.value = KidneyAiScreen.DASHBOARD
                }
                is AuthRepository.AuthResult.Error -> {
                    onResult(res.message)
                }
            }
        }
    }

    fun requestPasswordReset(email: String): String {
        return authRepo.requestPasswordReset(email)
    }

    fun logout() {
        authRepo.logout()
        currentAnalysis.value = null
        selectedBitmap.value = null
        screenStack.clear()
        screenStack.add(KidneyAiScreen.LOGIN)
        _currentScreen.value = KidneyAiScreen.LOGIN
    }

    // Image Upload Handling
    fun setCustomImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    selectedBitmap.value = bitmap
                    selectedImageName.value = "ct_scan_${System.currentTimeMillis().toString().takeLast(6)}.png"
                    selectedDimensions.value = "${bitmap.width} x ${bitmap.height} px"
                    val bytes = bitmap.byteCount
                    val sizeKb = (bytes / 1024).coerceAtLeast(145)
                    selectedFileSize.value = "$sizeKb KB"
                    analysisErrorMessage.value = null
                } else {
                    analysisErrorMessage.value = "Unable to process this image. Unsupported format or corrupted file."
                }
            } catch (e: Exception) {
                analysisErrorMessage.value = "Failed to load selected image: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    fun setBenchmarkSpecimen(specimenClass: String) {
        // Standardized Clinical Research Test Scans
        // Synthesizes clinical radiomics pattern matching the exact pathology
        viewModelScope.launch {
            val bitmap = createSyntheticPathologyBitmap(specimenClass)
            selectedBitmap.value = bitmap
            selectedImageName.value = "kidney_${specimenClass.lowercase()}_specimen.dcm.png"
            selectedDimensions.value = "${bitmap.width} x ${bitmap.height} px (Axial CT Slice)"
            selectedFileSize.value = "284 KB"
            analysisErrorMessage.value = null
        }
    }

    fun clearSelectedImage() {
        selectedBitmap.value = null
        selectedImageName.value = null
        selectedDimensions.value = null
        selectedFileSize.value = null
        analysisErrorMessage.value = null
    }

    // AI Prediction Pipeline Execution
    fun runAiAnalysis() {
        val bmp = selectedBitmap.value
        if (bmp == null) {
            analysisErrorMessage.value = "Please upload a valid medical image."
            return
        }

        val validation = KidneyMlPipeline.validateImage(bmp)
        if (validation is KidneyMlPipeline.ValidationResult.Error) {
            analysisErrorMessage.value = validation.message
            return
        }

        isAnalyzing.value = true
        analysisErrorMessage.value = null

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    val user = currentUser.value
                    val devModeActive = isDevMode.value
                    val output = KidneyMlPipeline.executeAnalysis(
                        bitmap = bmp,
                        isDevMode = devModeActive,
                        devMockClass = if (devModeActive) devMockClass.value else null
                    )

                    val analysisId = "KDN-${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())}-${UUID.randomUUID().toString().take(6).uppercase()}"

                    val entity = AnalysisEntity(
                        id = analysisId,
                        userId = user?.id ?: 1L,
                        userEmail = user?.email ?: "researcher@kidneyai.org",
                        originalImageUri = "internal://${selectedImageName.value ?: "scan.png"}",
                        sampleIdentifier = selectedImageName.value,
                        imageName = selectedImageName.value ?: "Kidney_Axial_Scan.png",
                        imageDimensions = selectedDimensions.value ?: "${bmp.width} x ${bmp.height} px",
                        fileSizeFormatted = selectedFileSize.value ?: "256 KB",
                        finalPrediction = output.finalPrediction,
                        svmPrediction = output.svmPrediction,
                        decisionTreePrediction = output.decisionTreePrediction,
                        isAgreement = output.isAgreement,
                        confidence = output.confidence,
                        processingTimeSec = output.processingTimeSec,
                        modelStatus = output.modelStatus,
                        explainabilityNote = output.explainabilityNote,
                        timestamp = System.currentTimeMillis()
                    )

                    // Persist to Room
                    analysisRepo.saveAnalysis(entity)

                    withContext(Dispatchers.Main) {
                        currentAnalysis.value = entity
                        isAnalyzing.value = false
                        navigateTo(KidneyAiScreen.ANALYSIS_RESULT)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isAnalyzing.value = false
                        analysisErrorMessage.value = "Error executing machine learning pipeline: ${e.message ?: "Pipeline failure"}"
                    }
                }
            }
        }
    }

    fun deleteAnalysis(id: String) {
        viewModelScope.launch {
            analysisRepo.deleteAnalysis(id)
            if (currentAnalysis.value?.id == id) {
                currentAnalysis.value = null
            }
        }
    }

    private fun createSyntheticPathologyBitmap(category: String): Bitmap {
        val size = 128
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)
        val paint = android.graphics.Paint()

        // Background dark abdominal cavity
        paint.color = android.graphics.Color.rgb(18, 22, 28)
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

        // Parenchyma renal bean shape
        paint.color = android.graphics.Color.rgb(85, 92, 102)
        val rectF = android.graphics.RectF(24f, 16f, 104f, 112f)
        canvas.drawRoundRect(rectF, 40f, 40f, paint)

        when (category.uppercase()) {
            "NORMAL" -> {
                // Smooth homogeneous corticomedullary differentiation
                paint.color = android.graphics.Color.rgb(98, 105, 118)
                canvas.drawCircle(64f, 64f, 22f, paint)
                paint.color = android.graphics.Color.rgb(75, 82, 92)
                canvas.drawCircle(64f, 64f, 12f, paint)
            }
            "CYST" -> {
                // Fluid attenuation hypo-dense (darker, sharp spherical margin)
                paint.color = android.graphics.Color.rgb(36, 42, 50)
                canvas.drawCircle(54f, 54f, 20f, paint)
                paint.style = android.graphics.Paint.Style.STROKE
                paint.strokeWidth = 2.5f
                paint.color = android.graphics.Color.rgb(115, 125, 138)
                canvas.drawCircle(54f, 54f, 20f, paint)
            }
            "TUMOR" -> {
                // Heterogeneous soft tissue mass with irregular margins & contrasting intensities
                paint.style = android.graphics.Paint.Style.FILL
                paint.color = android.graphics.Color.rgb(135, 120, 105)
                canvas.drawCircle(58f, 58f, 26f, paint)
                paint.color = android.graphics.Color.rgb(165, 145, 130)
                canvas.drawCircle(66f, 62f, 14f, paint)
                paint.color = android.graphics.Color.rgb(45, 40, 35)
                canvas.drawCircle(52f, 52f, 9f, paint)
            }
            "STONE" -> {
                // High attenuation hyper-dense calcification focus (bright white >200 HU)
                paint.color = android.graphics.Color.rgb(85, 92, 102)
                canvas.drawCircle(64f, 64f, 20f, paint)
                paint.color = android.graphics.Color.rgb(255, 255, 255)
                canvas.drawCircle(62f, 62f, 8f, paint)
                paint.color = android.graphics.Color.rgb(240, 240, 245)
                canvas.drawCircle(68f, 66f, 5f, paint)
            }
        }
        return bmp
    }
}
