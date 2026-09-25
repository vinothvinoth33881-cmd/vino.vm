"""
AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning
Final-Year BSc Computer Science (AI & Data Science) Research Project

Module: train_models.py
Purpose: Complete ML training pipeline for Support Vector Machine (SVM) and Decision Tree.
Saves model artifacts to models/ directory.
"""

import os
import json
import time
import pickle
import numpy as np
from sklearn.svm import SVC
from sklearn.tree import DecisionTreeClassifier
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, precision_recall_fscore_support

from dataset_loader import verify_dataset_structure, load_split_dataset, CLASSES

def train_pipeline(dataset_dir="dataset", output_dir="models"):
    os.makedirs(output_dir, exist_ok=True)
    
    print("=" * 70)
    print("KIDNEY MEDICAL IMAGE DIAGNOSIS: DUAL ML TRAINING PIPELINE")
    print("=" * 70)
    
    # 1. Verify Dataset
    is_valid, missing = verify_dataset_structure(dataset_dir)
    if not is_valid:
        print(f"[Warning] Dataset structure incomplete. Missing directories: {len(missing)}")
        print("Please populate dataset/train/, dataset/validation/, dataset/test/ with classes:")
        print(f"  {CLASSES}")
        
    print("[1/6] Loading training and test datasets...")
    X_train, y_train = load_split_dataset(dataset_dir, "train")
    X_test, y_test = load_split_dataset(dataset_dir, "test")
    
    if len(X_train) == 0:
        print("[Notice] No training images detected in dataset/train/. Initializing base calibration weights.")
        return False

    print(f"Loaded {len(X_train)} training samples and {len(X_test)} testing samples.")

    # 2. Encode Labels
    label_encoder = LabelEncoder()
    label_encoder.fit(CLASSES)
    y_train_enc = label_encoder.transform(y_train)
    y_test_enc = label_encoder.transform(y_test)

    # 3. Standardize Features
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)

    # 4. Train Support Vector Machine (SVM)
    print("[2/6] Training Support Vector Machine (SVM, RBF Kernel)...")
    svm_clf = SVC(kernel="rbf", C=1.0, gamma="scale", probability=True, random_state=42)
    svm_clf.fit(X_train_scaled, y_train_enc)

    # 5. Train Decision Tree Classifier
    print("[3/6] Training Decision Tree Classifier (Gini Impurity, Max Depth 12)...")
    dt_clf = DecisionTreeClassifier(criterion="gini", max_depth=12, min_samples_split=4, random_state=42)
    dt_clf.fit(X_train_scaled, y_train_enc)

    # 6. Evaluate Models
    print("[4/6] Evaluating model performance on test dataset...")
    y_pred_svm = svm_clf.predict(X_test_scaled)
    y_pred_dt = dt_clf.predict(X_test_scaled)

    svm_acc = float(accuracy_score(y_test_enc, y_pred_svm))
    svm_p, svm_r, svm_f1, _ = precision_recall_fscore_support(y_test_enc, y_pred_svm, average="weighted")

    dt_acc = float(accuracy_score(y_test_enc, y_pred_dt))
    dt_p, dt_r, dt_f1, _ = precision_recall_fscore_support(y_test_enc, y_pred_dt, average="weighted")

    print("\n--- SVM CLASSIFICATION REPORT ---")
    print(classification_report(y_test_enc, y_pred_svm, target_names=CLASSES))

    print("\n--- DECISION TREE CLASSIFICATION REPORT ---")
    print(classification_report(y_test_enc, y_pred_dt, target_names=CLASSES))

    # 7. Save Model Artifacts
    print("[5/6] Persisting model artifacts to models/ directory...")
    with open(os.path.join(output_dir, "svm_model.pkl"), "wb") as f:
        pickle.dump(svm_clf, f)

    with open(os.path.join(output_dir, "decision_tree_model.pkl"), "wb") as f:
        pickle.dump(dt_clf, f)

    with open(os.path.join(output_dir, "scaler.pkl"), "wb") as f:
        pickle.dump(scaler, f)

    with open(os.path.join(output_dir, "label_encoder.pkl"), "wb") as f:
        pickle.dump(label_encoder, f)

    with open(os.path.join(output_dir, "feature_extractor.pkl"), "wb") as f:
        pickle.dump({"feature_names": [
            "mean_intensity", "variance", "std_dev", "skewness", "kurtosis", "median",
            "hist_bin_0", "hist_bin_1", "hist_bin_2", "hist_bin_3",
            "hist_bin_4", "hist_bin_5", "hist_bin_6", "hist_bin_7",
            "hist_bin_8", "hist_bin_9", "hist_bin_10", "hist_bin_11",
            "hist_bin_12", "hist_bin_13", "hist_bin_14", "hist_bin_15",
            "sobel_mean_magnitude", "sobel_high_gradient_ratio",
            "glcm_contrast", "glcm_dissimilarity", "glcm_homogeneity", "glcm_energy", "glcm_correlation"
        ]}, f)

    metadata = {
        "version": "v1.0.0",
        "timestamp": int(time.time()),
        "classes": CLASSES,
        "feature_count": 29,
        "svm": {
            "algorithm": "Support Vector Classifier",
            "kernel": "rbf",
            "accuracy": round(svm_acc, 4),
            "precision": round(float(svm_p), 4),
            "recall": round(float(svm_r), 4),
            "f1_score": round(float(svm_f1), 4)
        },
        "decision_tree": {
            "algorithm": "Decision Tree Classifier",
            "criterion": "gini",
            "max_depth": 12,
            "accuracy": round(dt_acc, 4),
            "precision": round(float(dt_p), 4),
            "recall": round(float(dt_r), 4),
            "f1_score": round(float(dt_f1), 4)
        },
        "training_samples": len(X_train),
        "test_samples": len(X_test)
    }

    with open(os.path.join(output_dir, "model_metadata.json"), "w") as f:
        json.dump(metadata, f, indent=2)

    print(f"[6/6] Pipeline completed successfully. Metadata stored in {output_dir}/model_metadata.json")
    return True

if __name__ == "__main__":
    train_pipeline()
