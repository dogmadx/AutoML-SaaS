# AutoML-SaaS

## Setup

```bash
pip install -r requirements.txt
```

## Test run

Run from repository root:

```bash
PYTHONPATH=. python3 scripts/run_train.py
```

Alternative:

```bash
python3 -m scripts.run_train
```

- dataset: `docs/train.csv`
- task type: `regression`

## Classification run

Run from repository root:

```bash
python3 -m scripts.run_classification
```

- dataset: `docs/train.csv`
- task type: `classification`

## Outputs

- regression plots: `storage/experiments/test_run_artifacts/plots`
- classification plots: `storage/experiments/test_classification_artifacts/plots`
