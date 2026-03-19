import matplotlib.pyplot as plt
import numpy as np
import csv
import os

data = []
csv_path = "communities.csv"
with open(csv_path, "r") as f:
    reader = csv.DictReader(f)
    for row in reader:
        data.append(int(row["size"]))  # ← conversion en int directement

os.makedirs("../results/plots", exist_ok=True)
x = np.arange(len(data))
width = 0.35
fig, ax = plt.subplots(figsize=(max(10, len(data) * 0.8), 6))
fig, ax = plt.subplots(figsize=(10, 6))
ax.hist(data, bins=50, log=True, color="steelblue", edgecolor="white")
ax.set_xlabel("Taille des communautés")
ax.set_ylabel("Nombre de communautés (échelle log)")
ax.set_title("Distribution des tailles de communautés")
ax.grid(axis='y', linestyle='--', alpha=0.3)

plt.tight_layout()
plt.savefig("../results/plots/communities.png", dpi=150)
print("Graphique généré : ../results/plots/communities.png")
