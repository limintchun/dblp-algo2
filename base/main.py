import matplotlib.pyplot as plt
import numpy as np
import csv
import os

def uf():
    data = []
    csv_path = "uf.csv"
    with open(csv_path, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            data.append(int(row["size"]))

    os.makedirs("../results/plots", exist_ok=True)
    fig, ax = plt.subplots(figsize=(10, 6))
    
    bins = np.logspace(np.log10(min(data)), np.log10(max(data)), 50)
    
    ax.hist(data, bins=bins, log=True, color="steelblue", edgecolor="white")
    ax.set_xscale('log') 

    ax.set_xlabel("Taille des communautés (échelle log)")
    ax.set_ylabel("Nombre de communautés (échelle log)")
    ax.set_title("Distribution des tailles de communautés")
    ax.grid(axis='both', linestyle='--', alpha=0.3)

    plt.tight_layout()
    plt.savefig("../results/plots/uf.png", dpi=150)
    print("Graphique généré : ../results/plots/uf.png")

def cfc():
    data = []
    csv_path = "cfc.csv"
    with open(csv_path, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            data.append(int(row["size"]))

    os.makedirs("../results/plots", exist_ok=True)
    fig, ax = plt.subplots(figsize=(10, 6))

    bins = np.logspace(np.log10(data.min()), np.log10(data.max()), 50)

    ax.hist(data, bins=50, log=True, color="steelblue", edgecolor="white")
    ax.set_xlabel("Taille des communautés")
    ax.set_ylabel("Nombre de communautés (échelle log)")
    ax.set_title("Nombre d'occurence (échelle log)")
    ax.grid(axis='y', linestyle='--', alpha=0.3)

    plt.tight_layout()
    plt.savefig("../results/plots/cfc.png", dpi=150)
    print("Graphique généré : ../results/plots/cfc.png")


if __name__ == "__main__":
    uf()
    cfc()
