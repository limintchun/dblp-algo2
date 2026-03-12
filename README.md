# INFO-F-203 : Détection de communautés dans DBLP

***Attention ne pas décompresser base/dblp.xml.gz***

Commande pour compiler le parser :

```bash
javac DblpPublicationGenerator.java DblpParsingDemo.java
```

Commande pour exécuter le parser :

```bash
java DblpParsingDemo dblp-2026-01-01.xml.gz dblp.dtd --limit=N
```

*--limit=N* permet de se limiter aux *N* premières lignes
