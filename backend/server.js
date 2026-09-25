/**
 * AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning
 * Backend API Server (Node.js + Express)
 */

const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
const multer = require('multer');
const { spawn } = require('child_process');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

// Configure Multer for medical image uploads
const uploadDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir, { recursive: true });
}

const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, uploadDir),
  filename: (req, file, cb) => {
    const ext = path.extname(file.originalname).toLowerCase();
    const safeName = `scan_${Date.now()}_${Math.random().toString(36).substr(2, 6)}${ext}`;
    cb(null, safeName);
  }
});

const upload = multer({
  storage,
  limits: { fileSize: 15 * 1024 * 1024 }, // 15MB limit
  fileFilter: (req, file, cb) => {
    const allowed = ['.jpg', '.jpeg', '.png', '.dcm'];
    const ext = path.extname(file.originalname).toLowerCase();
    if (!allowed.includes(ext)) {
      return cb(new Error('Unsupported file format. Please upload JPG, PNG, or DICOM.'));
    }
    cb(null, true);
  }
});

// Model Status Endpoint
app.get('/api/model/status', (req, res) => {
  const metadataPath = path.join(__dirname, '..', 'models', 'model_metadata.json');
  if (fs.existsSync(metadataPath)) {
    const data = JSON.parse(fs.readFileSync(metadataPath, 'utf8'));
    return res.json({
      status: 'trained',
      svm_loaded: true,
      decision_tree_loaded: true,
      metadata: data
    });
  }
  res.json({
    status: 'development_mode',
    svm_loaded: false,
    decision_tree_loaded: false,
    message: 'Model files are not connected yet. Development Mode is active.'
  });
});

// Diagnostic Prediction Endpoint
app.post('/api/predict', upload.single('image'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: 'Please upload a valid medical image.' });
  }

  const imagePath = req.file.path;
  const pythonScript = path.join(__dirname, '..', 'ml', 'predict.py');

  const py = spawn('python3', [pythonScript, imagePath]);
  let output = '';
  let errorOutput = '';

  py.stdout.on('data', (data) => {
    output += data.toString();
  });

  py.stderr.on('data', (data) => {
    errorOutput += data.toString();
  });

  py.on('close', (code) => {
    // Clean temporary upload
    try {
      if (fs.existsSync(imagePath)) fs.unlinkSync(imagePath);
    } catch (_) {}

    if (code !== 0) {
      return res.status(500).json({
        error: 'Machine learning prediction process encountered an error.',
        details: errorOutput
      });
    }

    try {
      const result = JSON.parse(output.trim());
      res.json(result);
    } catch (e) {
      res.status(500).json({
        error: 'Failed to parse ML response.',
        raw: output
      });
    }
  });
});

app.listen(PORT, () => {
  console.log(`Kidney AI Diagnosis API Server active on port ${PORT}`);
});
