XML = dblp-2026-01-01.xml.gz
DTD = dblp.dtd

CLASSPATH = .:../lib/matplotlib4j-0.5.0.jar:../lib/slf4j-api-1.7.36.jar:../lib/slf4j-simple-1.7.36.jar

LIMIT = 1000000

compile:
	javac DblpPublicationGenerator.java DblpParsingDemo.java UnionFind.java

compilehist:
	javac -cp $(CLASSPATH) *.java

run: compile
	java -Xmx2g DblpParsingDemo $(XML) $(DTD) --limit=$(LIMIT)

run-full: compile
	java -Xmx2g DblpParsingDemo $(XML) $(DTD)

run-hist:
	java -cp $(CLASSPATH) Hist
clean:
	rm -f *.class
