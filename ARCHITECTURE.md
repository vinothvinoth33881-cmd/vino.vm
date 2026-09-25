# System Architecture & Technical Specifications

**Project:** AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning  
**Target:** BSc Computer Science with Artificial Intelligence and Data Science

---

## 1. Architectural Layers Overview

```
+-------------------------------------------------------------------------+
|                          PRESENTATION LAYER                             |
|  - Jetpack Compose Mobile Applet (Android M3, Edge-to-Edge, Dark Navy)  |
|  - React + TypeScript Web Dashboard (Optional Web Client)              |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                        APPLICATION & AUTH LAYER                         |
|  - ViewModel & State Management (StateFlow / Coroutines)                |
|  - Repository Pattern (AuthRepository, AnalysisRepository)              |
|  - Cryptographic Salted SHA-256 Authentication Engine                    |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                   RADIOMICS PREPROCESSING PIPELINE                      |
|  - Image Dimension & Header Validation                                 |
|  - Luminance Grayscale Conversion (0.299R + 0.587G + 0.114B)            |
|  - 64x64 Rescaling & Min-Max [0.0, 1.0] Intensity Normalization         |
|  - 29-Dimensional Feature Vector Extraction                            |
|    * Statistical Moments (Mean, Variance, Std, Skewness, Kurtosis, Med)  |
|    * 16-Bin Intensity Histogram Distribution                            |
|    * Spatial Sobel Edge Gradient Magnitude                              |
|    * 8-Level GLCM Radiomics (Contrast, Homogeneity, Energy, Corr)       |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                        DUAL ML INFERENCE ENGINES                        |
|  1. Support Vector Machine (SVM)                                        |
|     - One-vs-Rest (OvR) Hyperplanes                                     |
|     - Dual Coefficient Decision Scores                                  |
|  2. Decision Tree Classifier                                            |
|     - Orthogonal CART Splitting                                         |
|     - Depth-controlled (Max Depth: 12, Gini Impurity)                   |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                   CONCORDANCE & CONFIDENCE ARBITRATION                  |
|  - Model Agreement Verification (SVM == DT)                             |
|  - Disagreement Flagging ("MODEL DISAGREEMENT DETECTED")                |
|  - Calibrated Confidence Calculation (or null if ambiguous)            |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                    DATA PERSISTENCE & REPORT LAYER                      |
|  - Room Local SQLite Database (users, analyses, model_metadata)         |
|  - Structured Academic Dossier & Decision-Support Report Generator       |
+-------------------------------------------------------------------------+
```

---

## 2. Radiomics Feature Vector Specification

The radiomics pipeline extracts exactly 29 features:

| Index | Feature Key | Mathematical Description | Clinical Significance |
|---|---|---|---|
| `0` | `mean_intensity` | $\mu = \frac{1}{N}\sum x_i$ | Tissue radiodensity / attenuation |
| `1` | `variance` | $\sigma^2 = \frac{1}{N}\sum (x_i - \mu)^2$ | Heterogeneity of renal mass |
| `2` | `std_dev` | $\sigma = \sqrt{\sigma^2}$ | Intensity spread |
| `3` | `skewness` | $\gamma_1 = \frac{1}{N\sigma^3}\sum (x_i - \mu)^3$ | Asymmetry of attenuation distribution |
| `4` | `kurtosis` | $\gamma_2 = \frac{1}{N\sigma^4}\sum (x_i - \mu)^4 - 3$ | Heavy tails in histogram (calcifications) |
| `5` | `median` | $50\text{th percentile}$ | Central radiodensity value |
| `6-21` | `hist_bin_0..15` | Normalized 16-bin histogram | Fraction of pixels across HU ranges |
| `22` | `sobel_mean_magnitude` | $\frac{1}{M}\sum \sqrt{G_x^2 + G_y^2}$ | Average boundary sharpness |
| `23` | `sobel_high_gradient_ratio` | $\frac{\text{Count}(\|\nabla\| > 0.35)}{M}$ | Proportion of sharp contrast boundaries |
| `24` | `glcm_contrast` | $\sum_{i,j} p(i,j)(i-j)^2$ | Local gray-level intensity variations |
| `25` | `glcm_dissimilarity` | $\sum_{i,j} p(i,j)\|i-j\|$ | Linear local variation measure |
| `26` | `glcm_homogeneity` | $\sum_{i,j} \frac{p(i,j)}{1 + (i-j)^2}$ | Uniformity of internal parenchyma |
| `27` | `glcm_energy` | $\sum_{i,j} p(i,j)^2$ | Angular second moment (orderliness) |
| `28` | `glcm_correlation` | $\sum_{i,j} \frac{(i-\mu_i)(j-\mu_j)p(i,j)}{\sigma_i \sigma_j}$ | Linear dependency of pixel pairs |

---

## 3. Database Schema (Room / SQLite)

### Users Table (`users`)
- `id`: INTEGER PRIMARY KEY AUTOINCREMENT
- `fullName`: TEXT NOT NULL
- `email`: TEXT NOT NULL UNIQUE
- `passwordHash`: TEXT NOT NULL
- `salt`: TEXT NOT NULL
- `role`: TEXT NOT NULL
- `institution`: TEXT NOT NULL
- `createdAt`: INTEGER NOT NULL

### Analyses Table (`analyses`)
- `id`: TEXT PRIMARY KEY (e.g. `KDN-2026-XXXX`)
- `userId`: INTEGER NOT NULL
- `userEmail`: TEXT NOT NULL
- `originalImageUri`: TEXT NOT NULL
- `sampleIdentifier`: TEXT
- `imageName`: TEXT NOT NULL
- `imageDimensions`: TEXT NOT NULL
- `fileSizeFormatted`: TEXT NOT NULL
- `finalPrediction`: TEXT NOT NULL (`NORMAL`, `CYST`, `TUMOR`, `STONE`, or `MODEL_DISAGREEMENT`)
- `svmPrediction`: TEXT NOT NULL
- `decisionTreePrediction`: TEXT NOT NULL
- `isAgreement`: INTEGER (BOOLEAN) NOT NULL
- `confidence`: REAL (NULLABLE, strictly null if uncalibrated)
- `processingTimeSec`: REAL NOT NULL
- `modelStatus`: TEXT NOT NULL (`TRAINED` or `DEVELOPMENT_MODE`)
- `explainabilityNote`: TEXT NOT NULL
- `timestamp`: INTEGER NOT NULL

### Model Metadata Table (`model_metadata`)
- `version`: TEXT PRIMARY KEY
- `svmStatus`: TEXT
- `dtStatus`: TEXT
- `scalerStatus`: TEXT
- `featureExtractorStatus`: TEXT
- `labelEncoderStatus`: TEXT
- `svmAccuracy`: REAL
- `dtAccuracy`: REAL
- `trainingDatasetSize`: INTEGER
- `lastUpdated`: INTEGER

---

## 4. Academic Viva & Evaluation Integrity

1. **Deterministic Feature Processing:** Mathematical parity guarantees identical outputs across the Kotlin Android applet and the Python scikit-learn training environment.
2. **Absence of Fabricated Predictions:** Predictions are derived directly from the mathematical models.
3. **Transparent Medical Ethics:** Prominently communicates that this platform is a decision-support prototype and not a replacement for clinical radiologist interpretation.
