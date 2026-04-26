import matplotlib.pyplot as plt
import numpy as np
import csv
import os


def uf():
    sizes = []
    counts = []
    csv_path = "uf.csv"
    with open(csv_path, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            sizes.append(int(row["community_size"]))
            counts.append(int(row["count"]))

    sizes_array = np.array(sizes)
    counts_array = np.array(counts)

    os.makedirs("../results/plots", exist_ok=True)
    fig, ax = plt.subplots(figsize=(10, 6))

    bins = np.logspace(np.log10(sizes_array.min()), np.log10(sizes_array.max() * 2), 50)

    ax.hist(sizes_array, bins=bins, weights=counts_array, log=True,
            color="steelblue", edgecolor="white")
    ax.set_xscale('log')
    ax.set_xlabel("Taille des communautés (échelle log)")
    ax.set_ylabel("Nombre de communautés (échelle log)")
    ax.set_title("Distribution des tailles de communautés")
    ax.grid(axis='both', linestyle='--', alpha=0.3)
    plt.tight_layout()
    plt.savefig("../results/plots/uf.png", dpi=150)
    print("Graphique généré : ../results/plots/uf.png")


def cfc():
    sizes = []
    counts = []
    csv_path = "cfc.csv"
    with open(csv_path, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            sizes.append(int(row["community_size"]))
            counts.append(int(row["count"]))

    sizes_array = np.array(sizes)
    counts_array = np.array(counts)

    os.makedirs("../results/plots", exist_ok=True)
    fig, ax = plt.subplots(figsize=(10, 6))

    ax.hist(sizes_array, bins=30, weights=counts_array, log=True,
            color="steelblue", edgecolor="white")
    ax.set_xlabel("Taille des communautés")
    ax.set_ylabel("Nombre de communautés (échelle log)")
    ax.set_title("Distribution des tailles de communautés après filtrage (tâche 2)(échelle log)")
    ax.grid(axis='both', linestyle='--', alpha=0.3)
    plt.tight_layout()
    plt.savefig("../results/plots/cfc.png", dpi=150)
    print("Graphique généré : ../results/plots/cfc.png")


if __name__ == "__main__":
    uf()
    cfc()
