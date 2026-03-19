# INFO-F-203 : Détection de communautés dans DBLP

## Description

L'objectif est de pouvoir déterminer les différentes communautés de chercheur-euse-s sur base de deux critères :
  - les communautés de co-publication : groupes de personne ayaynt une chaine de co-publications reliants les auteur-rice-s.
  - les communautés : groupes de chercheur-euse-s  ayant au minimum 6 publications en commun.

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

Lancer le script de visualisation :

```bash
python3 base/main.py
```

L'histogramme sera sauvegardé dans `results/plots/communities.png`.
