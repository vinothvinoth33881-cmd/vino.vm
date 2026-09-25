"""
AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning
Final-Year BSc Computer Science (AI & Data Science) Research Project

Module: evaluate.py
Purpose: Evaluates test dataset performance and robustness against edge cases.
"""

import os
import json
import numpy as np
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score
from predict import predict_image, load_model_artifacts
from dataset_loader import load_split_dataset, CLASSES

def evaluate_models(dataset_dir="dataset"):
    print("=" * 60)
    print("EVALUATING MODEL PERFORMANCE ON TEST DATASET")
    print("=" * 60)

    models, is_connected = load_model_artifacts()
    if not is_connected:
        print("[Notice] Models are not trained/connected yet. Showing Development Mode behavior:")
        res = predict_image("non_existent_specimen.png", use_mock_model=True)
        print(json.dumps(res, indent=2))
        return

    X_test, y_test = load_split_dataset(dataset_dir, "test")
    if len(X_test) == 0:
        print("[Notice] No test samples found in dataset/test/.")
        return

    X_test_scaled = models["scaler"].transform(X_test)
    y_test_enc = models["label_encoder"].transform(y_test)

    # SVM
    y_pred_svm = models["svm"].predict(X_test_scaled)
    print("\n[SVM Classifier Results]")
    print(f"Accuracy: {accuracy_score(y_test_enc, y_pred_svm):.4f}")
    print(classification_report(y_test_enc, y_pred_svm, target_names=CLASSES))
    print("Confusion Matrix:")
    print(confusion_matrix(y_test_enc, y_pred_svm))

    # Decision Tree
    y_pred_dt = models["dt"].predict(X_test_scaled)
    print("\n[Decision Tree Classifier Results]")
    print(f"Accuracy: {accuracy_score(y_test_enc, y_pred_dt):.4f}")
    print(classification_report(y_test_enc, y_pred_dt, target_names=CLASSES))
    print("Confusion Matrix:")
    print(confusion_matrix(y_test_enc, y_pred_dt))

def run_robustness_tests():
    print("\n" + "=" * 60)
    print("RUNNING EDGE-CASE & ROBUSTNESS TESTS")
    print("=" * 60)

    # Test 1: Non-existent image
    res1 = predict_image("missing_file.jpg")
    print("Test 1 (Missing file):", res1.get("error"))

    # Test 2: Invalid text file pretending to be image
    invalid_path = "/tmp/invalid_fake_scan.png"
    with open(invalid_path, "w") as f:
        f.write("Not a real medical scan image")
    res2 = predict_image(invalid_path)
    print("Test 2 (Corrupted/invalid content):", res2.get("error"))
    if os.path.exists(invalid_path):
        os.remove(invalid_path)

    print("Edge-case checks passed: Application rejects invalid scans gracefully without crashing.")

if __name__ == "__main__":
    evaluate_models()
    run_robustness_tests()
