# ML Output Contract (Universal)

```python
def train_pipeline(
    dataset_path: str,
    target: str,
    task_type: str,   # "classification" | "regression" | "clustering" | ...
    options: dict,
    out_dir: str
) -> dict:
    ...
```

## Return shape

```python
{
    # Always present
    "status": "ok" | "failed",
    "task_type": str,
    "metrics": dict[str, float | int | str],
    "plots": dict[str, {
        "type": str,               # e.g. "scatter", "hist", "line", "bar"
        "data": dict,              # flexible payload: x/y/values/labels/series/etc.
        "labels": dict[str, str],  # e.g. x, y, title, legend
        "meta": dict               # optional plot-specific params
    }],
    "error": {
        "code": str,
        "message": str
    } | None,

    # Optional
    "artifacts"?: dict[str, str],
    "data_summary"?: dict[str, int | float]
}
```

## Rules

- `metrics` and `plots` are open dictionaries: no fixed metric or plot names are required.
- `artifacts` and `data_summary` are optional and can be omitted if not needed.
