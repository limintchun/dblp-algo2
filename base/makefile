XML = dblp-2026-01-01.xml.gz
DTD = dblp.dtd

LIMIT = 1000000
compile:
	javac DblpPublicationGenerator.java DblpParsingDemo.java UnionFind.java

run: compile
	java -Xmx2g DblpParsingDemo $(XML) $(DTD) --limit=$(LIMIT)

run-full: compile
	java -Xmx2g DblpParsingDemo $(XML) $(DTD)

clean:
	rm -f *.class
