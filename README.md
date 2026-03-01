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

## Output

After a successful run:
- metrics are printed in console (`mae`, `rmse`, `r2`)
- plots are saved to `storage/experiments/test_run_artifacts/plots`
  - `actual_vs_pred.png`
  - `residuals_hist.png`
  - `residuals_vs_pred.png`

## Notes