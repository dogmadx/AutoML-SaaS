from pathlib import Path
import os

os.environ.setdefault("MPLCONFIGDIR", "/tmp/matplotlib")
import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

from ml.pipeline import train_pipeline


def save_plots_from_result(result: dict, out_dir: str) -> None:
    # SAVE PLOTS FROM RESULT CONFIG
    plots = result.get("plots", {})
    plots_dir = Path(out_dir) / "plots"
    plots_dir.mkdir(parents=True, exist_ok=True)

    for plot_name, cfg in plots.items():
        plot_type = cfg.get("type")
        data = cfg.get("data", {})
        labels = cfg.get("labels", {})

        plt.figure(figsize=(8, 5))
        if plot_type == "scatter":
            plt.scatter(data.get("x", []), data.get("y", []), s=18, alpha=0.7)
        elif plot_type == "hist":
            plt.hist(data.get("values", []), bins=30, alpha=0.8)
        elif plot_type == "matrix":
            matrix = data.get("matrix", [])
            meta = cfg.get("meta", {})
            x_tick_labels = meta.get("x_tick_labels", [])
            y_tick_labels = meta.get("y_tick_labels", [])

            image = plt.imshow(matrix, cmap="Blues", aspect="auto")
            plt.colorbar(image, fraction=0.046, pad=0.04)

            for i, row in enumerate(matrix):
                for j, value in enumerate(row):
                    plt.text(j, i, str(value), ha="center", va="center", color="black")

            if x_tick_labels:
                plt.xticks(range(len(x_tick_labels)), x_tick_labels)
            if y_tick_labels:
                plt.yticks(range(len(y_tick_labels)), y_tick_labels)
        else:
            plt.close()
            continue

        plt.title(labels.get("title", plot_name))
        plt.xlabel(labels.get("x", ""))
        plt.ylabel(labels.get("y", ""))
        plt.tight_layout()
        plt.savefig(plots_dir / f"{plot_name}.png", dpi=140)
        plt.close()


OUT_DIR = "storage/experiments/test_classification_artifacts"

result = train_pipeline(
    dataset_path="docs/train.csv",
    target="Survived",
    task_type="classification",
    options={"test_size": 0.2, "random_state": 42},
    out_dir=OUT_DIR,
)

save_plots_from_result(result, OUT_DIR)
print({"status": result.get("status"), "metrics": result.get("metrics")})
print(f"Saved plots to: {Path(OUT_DIR) / 'plots'}")
