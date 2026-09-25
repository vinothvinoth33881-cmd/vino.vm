# Kidney CT & MRI Pathology Dataset Structure

The Kidney AI Diagnosis system expects images structured into exactly four pathological classes:

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

## Class Definitions

1. **normal**: Healthy renal parenchyma with regular corticomedullary differentiation and absence of focal lesions, masses, or calcifications.
2. **cyst**: Fluid-attenuated benign renal cysts with low Hounsfield Unit (HU 0-20) density and smooth, well-defined spherical borders.
3. **tumor**: Renal cell carcinoma (RCC) or other solid renal parenchymal masses demonstrating internal density heterogeneity, contrast enhancement, and irregular contours.
4. **stone**: Renal calculi / nephrolithiasis exhibiting marked hyperdensity (>200-400 HU) with distinct acoustic shadowing or sharp calcified contours.

## Permitted Image Formats
- DICOM (`.dcm`)
- PNG (`.png`)
- JPEG / JPG (`.jpg`, `.jpeg`)
