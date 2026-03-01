import pandas as pd
from math import sqrt

from sklearn.linear_model import LinearRegression
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder
from sklearn.impute import SimpleImputer
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score


def train_pipeline(dataset_path, target, task_type, options, out_dir) -> dict:
    # LOAD DATASET FROM CSV FILE
    df = pd.read_csv(dataset_path)

    # CHECK TARGET IS VALID COLUMN
    target_exists = target in df.columns
    if not target_exists:
        return {
            "status": "failed",
            "task_type": task_type,
            "metrics": {},
            "plots": {},
            "error": {
                "code": "TARGET_NOT_FOUND",
                "message": f"target column '{target}' not found",
            },
        }

    # DELETE ROWS WHERE TARGET IS NA
    df = df.dropna(subset=[target])

    # SPLIT FEATURES (X) AND TARGET (y)
    X = df.drop(columns=[target])
    y = df[target]

    # TRAIN/TEST SPLIT DATA
    test_size = options.get("test_size", 0.2)
    random_state = options.get("random_state", 42)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=test_size, random_state=random_state
    )

    # SELECT NUMERIC COLUMNS FOR MEDIAN IMPUTATION
    numeric_cols = X.select_dtypes(include=["number"]).columns.tolist()
    # SELECT NON-NUMERIC COLUMNS FOR ONE-HOT ENCODING
    categorical_cols = X.select_dtypes(exclude=["number"]).columns.tolist()

    # BUILD PREPROCESSOR FOR NUMERIC + CATEGORICAL FEATURES
    preprocess = ColumnTransformer(
        transformers=[
            ("num", SimpleImputer(strategy="median"), numeric_cols),
            ("cat", OneHotEncoder(handle_unknown="ignore"), categorical_cols),
        ]
    )

    # MODEL SELECT
    if task_type == "regression":
        model = LinearRegression()
    elif task_type == "classification":
        pass

    else:
        return {
            "status": "failed",
            "task_type": task_type,
            "metrics": {},
            "plots": {},
            "error": {
                "code": "UNSUPPORTED_TASK_TYPE",
                "message": f"unsupported task_type '{task_type}'",
            },
        }

    # PIPELINE
    pipe = Pipeline(
        [
            ("preprocess", preprocess),
            ("model", model),
        ]
    )

    pipe.fit(X_train, y_train)
    y_pred = pipe.predict(X_test)

    # RETURN
    if task_type == "regression":
        mae = mean_absolute_error(y_test, y_pred)
        mse = mean_squared_error(y_test, y_pred)
        r2 = r2_score(y_test, y_pred)
        rmse = sqrt(mse)
        residuals = y_test - y_pred

        return {
            "status": "ok",
            "task_type": "regression",
            "metrics": {
                "mae": mae,
                "rmse": rmse,
                "r2": r2,
            },
            "plots": {
                "actual_vs_pred": {
                    "type": "scatter",
                    "data": {
                        "x": y_test.tolist(),
                        "y": y_pred.tolist(),
                    },
                    "labels": {
                        "x": "Actual",
                        "y": "Predicted",
                        "title": "Actual vs Predicted",
                    },
                    "meta": {},
                },
                "residuals_hist": {
                    "type": "hist",
                    "data": {
                        "values": residuals.tolist(),
                    },
                    "labels": {
                        "x": "Residual",
                        "y": "Count",
                        "title": "Residuals Distribution",
                    },
                    "meta": {},
                },
                "residuals_vs_pred": {
                    "type": "scatter",
                    "data": {
                        "x": y_pred.tolist(),
                        "y": residuals.tolist(),
                    },
                    "labels": {
                        "x": "Predicted",
                        "y": "Residual",
                        "title": "Residuals vs Predicted",
                    },
                    "meta": {},
                },
            },
            "error": None,
        }

    return {
        "status": "failed",
        "task_type": task_type,
        "metrics": {},
        "plots": {},
        "error": {
            "code": "UNEXPECTED_STATE",
            "message": "unexpected pipeline state",
        },
    }
