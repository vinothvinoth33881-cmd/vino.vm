"""
AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning
Final-Year BSc Computer Science (AI & Data Science) Research Project

Module: feature_extraction.py
Purpose: Radiomics feature extraction for renal CT/MRI scans.
Extracts 29 engineered features matching Kotlin client-side execution.
"""

import numpy as np
from scipy import stats

FEATURE_NAMES = [
    "mean_intensity", "variance", "std_dev", "skewness", "kurtosis", "median",
    "hist_bin_0", "hist_bin_1", "hist_bin_2", "hist_bin_3",
    "hist_bin_4", "hist_bin_5", "hist_bin_6", "hist_bin_7",
    "hist_bin_8", "hist_bin_9", "hist_bin_10", "hist_bin_11",
    "hist_bin_12", "hist_bin_13", "hist_bin_14", "hist_bin_15",
    "sobel_mean_magnitude", "sobel_high_gradient_ratio",
    "glcm_contrast", "glcm_dissimilarity", "glcm_homogeneity", "glcm_energy", "glcm_correlation"
]

def extract_features(normalized_image):
    """
    Extracts 29 radiomics features from a 2D float32 normalized image array (64x64).
    """
    flat = normalized_image.flatten()
    
    # 1. Statistical Moments
    mean_val = float(np.mean(flat))
    var_val = float(np.var(flat))
    std_val = float(np.std(flat))
    skew_val = float(stats.skew(flat)) if std_val > 1e-6 else 0.0
    kurt_val = float(stats.kurtosis(flat)) if std_val > 1e-6 else 0.0
    median_val = float(np.median(flat))
    
    # 2. 16-bin Normalized Intensity Histogram
    hist, _ = np.histogram(flat, bins=16, range=(0.0, 1.0), density=False)
    hist_norm = (hist / float(len(flat))).tolist()
    
    # 3. Spatial Sobel Gradients
    # Sobel kernels
    gx = np.array([[-1, 0, 1], [-2, 0, 2], [-1, 0, 1]], dtype=np.float32)
    gy = np.array([[-1, -2, -1], [0, 0, 0], [1, 2, 1]], dtype=np.float32)
    
    h, w = normalized_image.shape
    edge_mags = []
    high_edge_count = 0
    
    for y in range(1, h - 1):
        for x in range(1, w - 1):
            sub = normalized_image[y-1:y+2, x-1:x+2]
            sx = np.sum(sub * gx)
            sy = np.sum(sub * gy)
            mag = float(np.sqrt(sx * sx + sy * sy))
            edge_mags.append(mag)
            if mag > 0.35:
                high_edge_count += 1
                
    sobel_mean = float(np.mean(edge_mags)) if edge_mags else 0.0
    sobel_high_ratio = float(high_edge_count / len(edge_mags)) if edge_mags else 0.0
    
    # 4. GLCM Radiomics Descriptors (8 quantized gray levels, dx=1, dy=0)
    levels = 8
    quantized = np.clip((normalized_image * (levels - 1)).astype(np.int32), 0, levels - 1)
    glcm = np.zeros((levels, levels), dtype=np.float32)
    pair_count = 0
    
    for y in range(h):
        for x in range(w - 1):
            i = quantized[y, x]
            j = quantized[y, x + 1]
            glcm[i, j] += 1.0
            glcm[j, i] += 1.0
            pair_count += 2
            
    if pair_count > 0:
        glcm /= pair_count
        
    contrast = 0.0
    dissimilarity = 0.0
    homogeneity = 0.0
    energy = 0.0
    mean_i = 0.0
    mean_j = 0.0
    
    for i in range(levels):
        for j in range(levels):
            p = glcm[i, j]
            diff = float(i - j)
            contrast += p * (diff ** 2)
            dissimilarity += p * abs(diff)
            homogeneity += p / (1.0 + (diff ** 2))
            energy += p * p
            mean_i += i * p
            mean_j += j * p
            
    var_i = sum(glcm[i, j] * ((i - mean_i) ** 2) for i in range(levels) for j in range(levels))
    var_j = sum(glcm[i, j] * ((j - mean_j) ** 2) for i in range(levels) for j in range(levels))
    std_i = np.sqrt(var_i)
    std_j = np.sqrt(var_j)
    
    correlation = 0.0
    if std_i > 1e-5 and std_j > 1e-5:
        cov = sum(glcm[i, j] * (i - mean_i) * (j - mean_j) for i in range(levels) for j in range(levels))
        correlation = float(np.clip(cov / (std_i * std_j), -1.0, 1.0))
        
    feature_vector = [
        mean_val, var_val, std_val, skew_val, kurt_val, median_val,
        *hist_norm,
        sobel_mean, sobel_high_ratio,
        contrast, dissimilarity, homogeneity, energy, correlation
    ]
    
    return np.array(feature_vector, dtype=np.float32)
