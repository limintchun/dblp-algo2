# Variables pour faciliter la maintenance
JC        = javac
JVM       = java
CP        = ".:algs4.jar"
MAIN      = DblpParsingDemo
XML       = dblp-2026-01-01.xml.gz
DTD       = dblp.dtd
# LIMIT   = 100000

# La cible par défaut (lance la compilation puis l'exécution)
all: run

# Cible pour compiler uniquement
compile:
	$(JC) -cp $(CP) $(MAIN).java

# Cible pour exécuter
# On ajoute la dépendance 'compile' pour s'assurer que le .class est à jour
run: compile
	$(JVM) -cp $(CP) $(MAIN) $(XML) $(DTD) # --limit=$(LIMIT)

# Cible pour nettoyer les fichiers compilés
clean:
	rm -f *.class
