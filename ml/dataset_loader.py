"""
AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning
Final-Year BSc Computer Science (AI & Data Science) Research Project

Module: dataset_loader.py
Purpose: Dataset directory verification, structured medical image loading, and batch extraction.
Classes: exactly normal, cyst, tumor, stone.
"""

import os
from PIL import Image
import numpy as np
from preprocessing import validate_image, preprocess_image
from feature_extraction import extract_features

CLASSES = ["normal", "cyst", "tumor", "stone"]

def verify_dataset_structure(base_path="dataset"):
    """
    Verifies that the dataset structure exists with all required pathology folders.
    """
    splits = ["train", "validation", "test"]
    missing = []
    
    for split in splits:
        split_dir = os.path.join(base_path, split)
        if not os.path.exists(split_dir):
            missing.append(split_dir)
            continue
        for cls in CLASSES:
            cls_dir = os.path.join(split_dir, cls)
            if not os.path.exists(cls_dir):
                missing.append(cls_dir)
                
    return len(missing) == 0, missing

def load_split_dataset(base_path="dataset", split="train"):
    """
    Loads images for a given split, executes identical preprocessing and feature extraction.
    Returns:
        X (np.ndarray): feature matrix of shape (N, 29)
        y (list): string class labels
    """
    X = []
    y = []
    split_dir = os.path.join(base_path, split)
    
    if not os.path.exists(split_dir):
        print(f"[DatasetLoader] Warning: {split_dir} does not exist.")
        return np.array(X), y
        
    for label in CLASSES:
        cls_folder = os.path.join(split_dir, label)
        if not os.path.exists(cls_folder):
            continue
            
        files = [f for f in os.listdir(cls_folder) if f.lower().endswith(('.png', '.jpg', '.jpeg', '.dcm'))]
        for f in files:
            img_path = os.path.join(cls_folder, f)
            valid, err, pil_img = validate_image(img_path)
            if not valid:
                print(f"[DatasetLoader] Skipping {img_path}: {err}")
                continue
                
            norm = preprocess_image(pil_img)
            feats = extract_features(norm)
            X.append(feats)
            y.append(label)
            
    return np.array(X, dtype=np.float32), y
