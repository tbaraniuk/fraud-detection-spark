# Fraud Detection using Apache Spark

Welcome to the `fraud-detection-spark` application! This project is designed to detect fraudulent transactions in financial data by leveraging the power of **Apache Spark** for distributed computing and **MLflow** for machine learning model tracking.

---

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Requirements and Dependencies](#requirements-and-dependencies)
- [Configuration](#configuration)
- [Usage](#usage)
- [Data](#data)
- [Model](#model)
- [License](#license)

---

## Features

- **Data Ingestion:** Handles raw financial transaction data input in CSV format.
- **Preprocessing:** 
  - Feature engineering (ex. PCA features and normalization).
  - Splitting data between `training` and `test` sets.
- **Machine Learning:**
  - Train fraud detection models using **Spark MLlib**.
  - Configurable logistic regression hyperparameters (e.g., max iterations, thresholds, etc.).
- **Model Tracking:**
  - Uses **MLflow** to log models, metrics, and hyperparameters.
- **Scalable Architecture:**
  - Built to scale financial data analysis using **Apache Spark**.

---

## Project Structure
```

fraud-detection-spark
├── data
│    └── raw                     # Raw input data (e.g., creditcard.csv)
├── models                        # Stored machine learning models
├── project                       # SBT project files
│    ├── build.properties
├── src
│    ├── main
│         ├── resources
│         │    └── application.conf  # Configuration file for the application
│         └── scala
│              └── com.fraud
│                   ├── app         # Main application entry
│                   ├── config      # Configuration classes
│                   ├── domain      # Core business logic/domain models
│                   ├── ingestion   # Data ingestion pipeline
│                   ├── ml          # Machine learning pipeline
│                   ├── preprocessing # Feature engineering and preprocessing logic
│                   ├── session     # Spark session setup
│                   ├── tracker     # MLflow tracking setup
│                   └── utils       # Utility classes and helpers
├── build.sbt                       # SBT build definition
├── Dockerfile                      # Docker configuration
├── LICENSE                         # License file
└── README.md                       # Project documentation (this file)
```
---

## Requirements and Dependencies

### Prerequisites

- **Java 11+**
- **Scala 2.12**
- **Apache Spark 3.5.0**
- **SBT (Scala Build Tool)**
- **MLflow Server**

### Dependencies

The following dependencies are defined in the `build.sbt` file:

- [PureConfig](https://github.com/pureconfig/pureconfig) for configuration management.
- [MLflow](https://mlflow.org/) for experiment tracking:
  - `mlflow-client`
  - `mlflow-spark`
- [Apache Spark](https://spark.apache.org/):
  - Core, SQL, MLlib, and Streaming libraries.

Run the following command to resolve and download these dependencies:
```
bash
sbt update
```
---

## Configuration

The application uses a centralized configuration file located at `src/main/resources/application.conf` to define:

- **Spark Configuration:**
  - `app-name`: Name of the application.
  - `master`: Spark master URL (e.g., `local[*]` for local development).
- **Input Data Configuration:**
  - `path`: Location of the raw data (default: `data/raw/creditcard.csv`).
  - `format`: Input data format (e.g., `csv`).
  - `options`: Additional parsing options (e.g., `header: true`).
- **Machine Learning Configuration:**
  - Features for PCA and numeric processing.
  - Hyperparameters (`max-iter`, `reg-param`, `elastic-net-param`, etc.).
  - Label column (e.g., `Class`).
- **MLflow Tracking Configuration:**
  - `tracking-uri`: Local or remote MLflow server URI.
- **Output Configuration:**
  - Path to store trained models (`models/logistic_regression_pipeline`).

Example configuration:
```
hocon
spark {
app-name: "FraudDetection"
master: "local[*]"
}

input {
path: "data/raw/creditcard.csv"
format: "csv"
options: {
"header": "true"
}
}
```
You can customize the configuration as needed before running the application.

---

## Usage

### Running the Application

1. Install dependencies:

   ```bash
   sbt compile
   ```

2. Run the application locally using SBT:

   ```bash
   sbt run
   ```

3. Optionally, package the application into a JAR file:

   ```bash
   sbt package
   ```

4. (Optional) Build and run using Docker:

   ```bash
   docker build -t fraud-detection-spark .
   docker run fraud-detection-spark
   ```

### Tracking Model Training with MLflow

Ensure the MLflow server is running:
```
bash
mlflow server --backend-store-uri sqlite:///mlflow.db --host 0.0.0.0 --port 5000
```
Once the training pipeline executes, you can view the logged models, metrics, and parameters in the MLflow UI (http://localhost:5000).

---

## Data

The project expects a dataset of transactions located at:
```

data/raw/creditcard.csv
```
### Dataset Format

The input data must be a **CSV file** with the following columns:

- `Time`: Time elapsed between transactions.
- `Amount`: Transaction amount.
- `V1` to `V28`: PCA-transformed features.
- `Class`: Target label (1 = Fraud, 0 = Non-fraud).

You can modify the dataset path in `application.conf`.

---

## Model

The default model used in this pipeline is **Logistic Regression**, which is trained on engineered features (e.g., PCA features) and hyper-tuned using the configurations in `application.conf`.

The trained model is saved to:
```

models/logistic_regression_pipeline
```
You can change the `model-path` in the configuration file before running the application.

---

## License


This project is licensed under the **[MIT License](./LICENSE)**. You are free to use, modify, and distribute this software in accordance with the license terms.

---