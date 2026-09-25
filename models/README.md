# Trained Machine Learning Model Artifacts

This directory stores the trained Python machine learning artifacts for the Kidney AI Diagnosis system:

```
models/
├── svm_model.pkl             # Trained scikit-learn Support Vector Machine (RBF/Linear)
├── decision_tree_model.pkl   # Trained scikit-learn Decision Tree (Gini, max_depth=12)
├── scaler.pkl                # StandardScaler fitted on 29 radiomics features
├── label_encoder.pkl         # LabelEncoder mapping classes: normal, cyst, tumor, stone
├── feature_extractor.pkl     # Radiomics feature definitions & metadata
└── model_metadata.json       # Evaluated metrics, hyperparameters, and class descriptions
```

## How to Generate/Update Model Files

To train the models from your local kidney CT dataset:

```bash
# 1. Ensure your dataset is arranged in dataset/train/, dataset/validation/, dataset/test/
# 2. Run the training script:
python ml/train_models.py
```

The script will automatically evaluate the models, generate the classification metrics, and save the binary `.pkl` files and `model_metadata.json` directly into this folder.

## Development Mode Notice

When `.pkl` files are not present in this folder, the system engages **Development Mode**:
- Informs the user: *"Model files are not connected yet. Development Mode is active."*
- Never presents mock predictions as clinical predictions.
