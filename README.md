# INFO-F-203 : Détection de communautés dans DBLP

## Description

L'objectif est de pouvoir déterminer les différentes communautés de chercheur-euse-s sur base de deux critères :
  - Les communautés de co-publication : groupes de personnes ayant une chaine de co-publications reliant les auteur-rice-s.
  - Les communautés : groupes de chercheur-euse-s ayant au minimum 6 publications en commun.

--- 

## Important
***Ne pas décompresser base/dblp.xml.gz***. Le parser lit directement le fichier compressé.

## Compilation et exécution du parser java

### Compilation
```bash
javac DblpPublicationGenerator.java DblpParsingDemo.java
```

### Exécution
```bash
java DblpParsingDemo dblp-2026-01-01.xml.gz dblp.dtd --limit=N
```
> `--limit=N` est optionnel et permet de se limiter aux *N* premières publications.

## Tâche 1

### Prérequis

Avoir exécuté le programme Java au préalable, ce qui génère le fichier `base/results.csv`
via la fonction `getSizeOfCommunities(UnionFind uf)`.

### Générer l'histogramme des tailles des communautés

Créer et activer un environnement virtuel Python, puis installer les dépendances :

```bash
python3 -m venv venv
source venv/bin/activate
pip install --upgrade pip && pip install matplotlib
```

***Remarque ; `venv` est créé dans le dossier courant donc il faut se placer à la racine
du projet ou dans le dossier `/base`.***

Lancer le script de visualisation :

```bash
python3 base/main.py
```

L'histogramme sera sauvegardé dans `results/plots/communities.png`.

## Tâche bonus

### Prérequis

Avoir exécuté le programme Java au préalable afin de générer le fichier 
`results/base/top10_scc_authors.csv` contenant les auteur-rice-s des 10 plus grandes communautés de la tâche B.

### Générer les graphiques d’association aux pays

Créer et activer un environnement virtuel Python, puis installer les dépendances :

```bash
python3 -m venv venv
source venv/bin/activate
pip install --upgrade pip && pip install matplotlib
```

***Remarque ; `venv` est créé dans le dossier courant donc il faut se placer à la racine
du projet ou dans le dossier `/base`.***

Lancer le script :

```bash
cd base
python3 openalex_countries.py
```

Le script utilise l'API OpenAlex pour rechercher l'institution associée à chaque auteur-rice 
puis en déduit le pays correspondant.

Deux fichiers sont générés :
- `results/plots/countries_affiliations.png` indiquant si chaque communauté est associée à un ou plusieurs pays.
- `results/plots/countries_proportions.png` indiquant la proportion d’auteur-rice-s par pays dans chaque communauté.

