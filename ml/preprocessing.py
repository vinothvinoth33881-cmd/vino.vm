"""
AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning
Final-Year BSc Computer Science (AI & Data Science) Research Project

Module: preprocessing.py
Purpose: Standardized image validation, resizing, grayscale conversion, and normalization.
Note: Training and prediction MUST execute identical preprocessing pipelines.
"""

import numpy as np
from PIL import Image

TARGET_WIDTH = 64
TARGET_HEIGHT = 64

def validate_image(image_path_or_bytes):
    """
    Validates input image file format, dimensions, and readability.
    Returns (is_valid: bool, error_msg: str, image: PIL.Image)
    """
    try:
        if isinstance(image_path_or_bytes, str):
            img = Image.open(image_path_or_bytes)
        else:
            img = Image.open(image_path_or_bytes)
        
        img.verify() # Verify file header and integrity
        # Re-open after verify
        if isinstance(image_path_or_bytes, str):
            img = Image.open(image_path_or_bytes)
        else:
            image_path_or_bytes.seek(0)
            img = Image.open(image_path_or_bytes)

        if img.width < 16 or img.height < 16:
            return False, "Image dimensions are too small for diagnostic analysis.", None

        return True, None, img
    except Exception as e:
        return False, f"Invalid or corrupted image: {str(e)}", None

def preprocess_image(pil_image):
    """
    Executes standard medical image preprocessing:
    1. Resize to target dimension (64x64) with anti-aliasing
    2. Convert to single-channel Grayscale (Luminance formula: 0.299R + 0.587G + 0.114B)
    3. Normalize pixel intensities into range [0.0, 1.0]
    
    Returns:
        np.ndarray of shape (64, 64) with dtype float32 in [0.0, 1.0]
    """
    # 1. Resize
    resized = pil_image.resize((TARGET_WIDTH, TARGET_HEIGHT), Image.Resampling.BILINEAR)
    
    # 2. Grayscale conversion
    if resized.mode != "L":
        gray = resized.convert("L")
    else:
        gray = resized

    # 3. Convert to float array and normalize
    gray_array = np.array(gray, dtype=np.float32)
    normalized = gray_array / 255.0
    
    return normalized
