def train_pipeline(
    dataset_path: str,
    target: str,
    task_type: str,   # "classification" | "regression"
    options: dict,
    out_dir: str      # куда сохранять артефакты
) -> dict:
    ...