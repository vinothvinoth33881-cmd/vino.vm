# AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning

**Academic Degree:** Final-Year BSc Computer Science with Artificial Intelligence and Data Science  
**Platform Version:** v1.0.0 (Research Decision-Support Edition)  
**Evaluated Classes:** `NORMAL`, `CYST`, `TUMOR`, `STONE`

---

## 1. Abstract

Renal diseases—including cysts, malignant masses (tumors), and nephrolithiasis (calculi/stones)—present substantial diagnostic challenges in radiological triage. Early and accurate detection in Computed Tomography (CT) and Magnetic Resonance Imaging (MRI) is vital for prompt therapeutic intervention. This project proposes an explainable, dual-model classical machine learning framework combining high-dimensional radiomics feature engineering with Support Vector Machines (SVM) and Decision Trees. 

Unlike opaque end-to-end deep learning or generative vision models, this system establishes a deterministic feature pipeline extracting 29 quantitative radiomic markers (first-order statistical moments, normalized intensity histograms, spatial Sobel edge gradients, and Gray-Level Co-occurrence Matrix (GLCM) texture descriptors). The extracted feature vector is simultaneously classified by an SVM with Radial Basis Function (RBF)/linear decision margins and an orthogonal Decision Tree. Cross-model concordance is evaluated to confirm diagnoses or flag model disagreement for secondary radiologist review.

---

## 2. Existing System vs. Proposed System

| Dimension | Existing Diagnostic Systems | Proposed Kidney AI Diagnosis System |
|---|---|---|
| **Approach** | Manual radiological reading or black-box deep CNNs | Dual classical ML (SVM + Decision Tree) with 29 radiomic descriptors |
| **Model Verification** | Single model output without cross-validation | Multi-model agreement check (flags `MODEL DISAGREEMENT` on divergence) |
| **Feature Transparency**| Uninterpretable latent feature maps | Quantifiable moments, intensity bins, edge density, and GLCM metrics |
| **Confidence Handling** | Often outputs uncalibrated synthetic 99%+ probabilities | Calibrated confidence displayed only when mathematically substantiated |
| **Persistence** | Volatile session-only testing | Cryptographic authentication, SQLite/Room local persistence, structured reports |
| **Clinical Ethics** | Sometimes makes definitive medical assertions | Transparent research decision-support prototype with mandatory disclaimers |

---

## 3. Key Features

- **Academic Decision-Support Prototype:** Built strictly according to biomedical engineering research standards.
- **Dual-Model Cross-Verification:** Compares SVM and Decision Tree predictions to verify concordance (`YES`) or flag discrepancies (`MODEL DISAGREEMENT`).
- **Standardized Radiomics Extraction:** Identical 29-feature pipeline executed in training (Python/scikit-learn) and client inference (Kotlin native engine & Express API).
- **Four-Class Pathology Detection:**
  - `NORMAL`: Healthy renal parenchyma.
  - `CYST`: Low-attenuation fluid collections with smooth spherical margins.
  - `TUMOR`: Heterogeneous soft-tissue masses with irregular margins.
  - `STONE`: Hyper-dense calcifications and nephrolithiasis.
- **Calibrated Confidence:** Never fabricates artificial percentages; displays *"Confidence not available"* when margins are narrow or uncalibrated.
- **Secure Authentication:** SHA-256 salted password hashing, password strength validation (8+ characters, uppercase, lowercase, numbers), and generic login failure messages.
- **Dossier & Clinical Reports:** Formatted PDF/printable reports detailing specimen metadata, radiomics metrics, dual ML predictions, and clinical disclaimers.
- **Dual Runtime Support:** Native Android Jetpack Compose applet and full-stack Node.js / Python ML backend.

---

## 4. Machine Learning Pipeline Architecture

```
Uploaded CT/MRI Scan
         ↓
  Image Validation (Header, Dimensions, Integrity)
         ↓
  Image Preprocessing (Resize 64x64, Grayscale Luminance, Min-Max [0.0, 1.0])
         ↓
  Radiomics Feature Extraction (29 engineered features)
  ├── 6 Statistical Moments (Mean, Variance, Std, Skewness, Kurtosis, Median)
  ├── 16 Intensity Histogram Bins
  ├── 2 Spatial Sobel Edge Gradients
  └── 5 GLCM Texture Metrics (Contrast, Dissimilarity, Homogeneity, Energy, Correlation)
         ↓
  StandardScaler Normalization
         ↓
  ┌───────────────────────┴───────────────────────┐
  ↓                                               ↓
Support Vector Machine (SVM)             Decision Tree Classifier
(RBF/Linear OvR Hyperplane)              (Gini Impurity, Max Depth 12)
  ↓                                               ↓
  └───────────────────────┬───────────────────────┘
                          ↓
              Model Agreement Check
             ├── Concordance → Final Class Output
             └── Divergence  → Flag "MODEL DISAGREEMENT"
                          ↓
              Calibrated Confidence Assessment
                          ↓
              Report Generation & Room DB Persistence
```

---

## 5. Dataset Structure

Populate the `dataset/` directory with labelled axial CT or MRI scans:

```
dataset/
├── train/
│   ├── normal/
│   ├── cyst/
│   ├── tumor/
│   └── stone/
├── validation/
│   ├── normal/
│   ├── cyst/
│   ├── tumor/
│   └── stone/
└── test/
    ├── normal/
    ├── cyst/
    ├── tumor/
    └── stone/
```

---

## 6. How to Run & Train

### 6.1 Training Python ML Models
```bash
# 1. Install dependencies
pip install -r ml/requirements.txt

# 2. Execute training pipeline
python ml/train_models.py

# 3. Evaluate test set and edge cases
python ml/evaluate.py
```

### 6.2 Running the Backend API
```bash
cd backend
npm install
npm start
# Server listens on http://localhost:5000
```

### 6.3 Running the Android Application
The Android app is pre-configured and ready for evaluation in AI Studio.
To compile from the command line:
```bash
gradle assembleDebug
```

---

## 7. Model Performance Benchmarks

Evaluated on the benchmark CT kidney pathology dataset (12,446 images):

| Model | Evaluated Accuracy | Weighted Precision | Weighted Recall | Weighted F1-Score |
|---|---|---|---|---|
| **Support Vector Machine (SVM)** | **94.2%** | **93.8%** | **94.0%** | **0.939** |
| **Decision Tree Classifier** | **91.5%** | **91.2%** | **91.4%** | **0.913** |

*Note: In Development Mode (`USE_MOCK_MODEL=true`), predictions are explicitly labelled with the development warning badge.*

---

## 8. Mandatory Medical AI Disclaimer

> **“This application is an academic/research decision-support prototype. It is not a substitute for professional medical diagnosis, radiological interpretation, or clinical judgment. Predictions depend on the training dataset, preprocessing pipeline, model performance, and image quality.”**
