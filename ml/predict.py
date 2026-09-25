"""
AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning
Final-Year BSc Computer Science (AI & Data Science) Research Project

Module: predict.py
Purpose: Inference service for incoming kidney CT/MRI images.
Executes identical preprocessing -> feature extraction -> SVM + Decision Tree inference.
"""

import os
import time
import json
import pickle
import numpy as np
from preprocessing import validate_image, preprocess_image
from feature_extraction import extract_features

MODELS_DIR = "models"
CLASSES = ["normal", "cyst", "tumor", "stone"]

def load_model_artifacts(models_dir=MODELS_DIR):
    """
    Attempts to load trained SVM, Decision Tree, scaler, and label encoder.
    Returns (artifacts_dict or None, is_connected: bool)
    """
    required_files = [
        "svm_model.pkl",
        "decision_tree_model.pkl",
        "scaler.pkl",
        "label_encoder.pkl"
    ]
    for rf in required_files:
        if not os.path.exists(os.path.join(models_dir, rf)):
            return None, False

    try:
        with open(os.path.join(models_dir, "svm_model.pkl"), "rb") as f:
            svm_clf = pickle.load(f)
        with open(os.path.join(models_dir, "decision_tree_model.pkl"), "rb") as f:
            dt_clf = pickle.load(f)
        with open(os.path.join(models_dir, "scaler.pkl"), "rb") as f:
            scaler = pickle.load(f)
        with open(os.path.join(models_dir, "label_encoder.pkl"), "rb") as f:
            label_encoder = pickle.load(f)
        return {
            "svm": svm_clf,
            "dt": dt_clf,
            "scaler": scaler,
            "label_encoder": label_encoder
        }, True
    except Exception as e:
        print(f"[Error loading models]: {e}")
        return None, False

def predict_image(image_path_or_bytes, use_mock_model=False, dev_mock_class="cyst"):
    start_time = time.time()
    
    # 1. Validation
    is_valid, err_msg, pil_img = validate_image(image_path_or_bytes)
    if not is_valid:
        return {
            "error": err_msg or "Please upload a valid medical image.",
            "status": "validation_failed"
        }

    # 2. Check Models
    models, is_connected = load_model_artifacts()
    
    if not is_connected or use_mock_model:
        # Development Mode
        elapsed = round(time.time() - start_time + 0.35, 2)
        return {
            "prediction": dev_mock_class,
            "svm_prediction": dev_mock_class,
            "decision_tree_prediction": dev_mock_class,
            "confidence": None, # Never fabricate confidence in mock mode!
            "model_status": "development_mode",
            "message": "Model files are not connected yet. Development Mode is active. Mock predictions are not medical predictions.",
            "processing_time": elapsed
        }

    # 3. Preprocessing (Exact same as training)
    normalized = preprocess_image(pil_img)
    
    # 4. Feature Extraction (Exact same 29 features)
    features = extract_features(normalized)
    
    # 5. Standardize Features
    features_scaled = models["scaler"].transform([features])

    # 6. SVM Inference
    svm_clf = models["svm"]
    svm_idx = svm_clf.predict(features_scaled)[0]
    svm_pred = models["label_encoder"].inverse_transform([svm_idx])[0]
    
    # Check calibrated SVM probability if available
    svm_conf = None
    if hasattr(svm_clf, "predict_proba"):
        probs = svm_clf.predict_proba(features_scaled)[0]
        top_prob = float(probs[svm_idx])
        probs_sorted = sorted(probs, reverse=True)
        margin = probs_sorted[0] - probs_sorted[1]
        if margin > 0.15 and top_prob >= 0.60:
            svm_conf = round(top_prob * 100.0, 1)

    # 7. Decision Tree Inference
    dt_clf = models["dt"]
    dt_idx = dt_clf.predict(features_scaled)[0]
    dt_pred = models["label_encoder"].inverse_transform([dt_idx])[0]
    
    dt_conf = None
    if hasattr(dt_clf, "predict_proba"):
        dt_probs = dt_clf.predict_proba(features_scaled)[0]
        top_dt_prob = float(dt_probs[dt_idx])
        if top_dt_prob >= 0.65:
            dt_conf = round(top_dt_prob * 100.0, 1)

    # 8. Compare Model Agreement
    is_agreement = (svm_pred.lower() == dt_pred.lower())
    final_pred = svm_pred if is_agreement else "MODEL_DISAGREEMENT"
    
    # Calibrated confidence only if models agree and probability is valid
    final_confidence = None
    if is_agreement:
        if svm_conf is not None and dt_conf is not None:
            final_confidence = round((svm_conf + dt_conf) / 2.0, 1)
        elif svm_conf is not None:
            final_confidence = svm_conf
        elif dt_conf is not None:
            final_confidence = dt_conf
            
    elapsed = round(time.time() - start_time, 2)
    
    return {
        "prediction": final_pred,
        "svm_prediction": svm_pred,
        "decision_tree_prediction": dt_pred,
        "model_agreement": is_agreement,
        "confidence": final_confidence, # None if not reliably available
        "model_status": "trained",
        "processing_time": elapsed
    }

if __name__ == "__main__":
    import sys
    test_img = sys.argv[1] if len(sys.argv) > 1 else None
    if test_img:
        print(json.dumps(predict_image(test_img), indent=2))
    else:
        print("Usage: python predict.py <path_to_image>")
