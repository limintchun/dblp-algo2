import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.KosarajuSharirSCC;
// on a le droit de l'utiliser mais il faut le citer dans le rapport

/**
 * Usage:
 *   java -Xmx2g DblpParsingDemo <dblp.xml|dblp.xml.gz> <dblp.dtd> [--limit=1000000]
 */
public class DblpParsingDemo {
    /**
     * @param graph le graphe orienté contenant les auteurs ayant minimum 6 publications en communs
     * @param source le sommet sur lequel on veut commencer le calcul du plus court chemin
     * @param destination le sommet sur lequel on veut arriver
     *
     * Calcule le plus cours chemin
     */
    private static int BFS(Digraph graph, int source, int destination) {
        Queue<Integer> q = new LinkedList<>();
        int[] distTo = new int[graph.V()];
        Arrays.fill(distTo, -1);

        distTo[source] = 0;
        q.add(source);

        while (!q.isEmpty()) {
            int v = q.poll();
            if (v == destination) {
                return distTo[v];
            }

            for (int w : graph.adj(v)) {
                if (distTo[w] == -1) {
                    distTo[w] = distTo[v] + 1;
                    q.add(w);
                }
            }
        }
        return -1;
    }

    /*
     * @param uf Regroupements d'auteurs
     * @return Une liste d'entiers conteant les tailles des 10 plus grandes communautés triée par ordre décroissant
     *
     * Calcule et extrait la taille des 10 plus grandes communautés identifées par la structure Union-Find.
     */
    private static List<Integer> getTop10(UnionFind uf) {
        return processTop10(uf.sizeOfComm().values());
    }

    /**
     * @param scc Contient les identifiants de composante pour chaque sommet
     * @param numVertices Le nombre total de sommets à parcourir dans le graphe
     * @return Une liste des tailles des 10 plus grandes commposantes fortement connexes, triée par ordre décroissant
     *
     * Calcule et extrait la taille des 10 plus grandes communautés identifées par la structure SCC.
     */
    private static List<Integer> getTop10(KosarajuSharirSCC scc, int numVertices) {
        int m = scc.count();
        int[] counts = new int[m];
        for (int v = 0; v < numVertices; v++) {
            counts[scc.id(v)]++;
        }

        List<Integer> sizes = new ArrayList<>();
        for (int s : counts) sizes.add(s);
        return processTop10(sizes);
    }

    /**
     * @param allSizes Collection contenant les tailles de toutes les communautés identifées
     * @return Une liste contenant les 10 tailles les plus élevées, triée par ordre décroissant
     *
     * Extrait les 10 pulus grandes valeurs d'une collection de tailles
     */
    private static List<Integer> processTop10(Collection<Integer> allSizes) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(11);
        for (int size : allSizes) {
            minHeap.offer(size);
            if (minHeap.size() > 10) minHeap.poll();
        }
        List<Integer> top10 = new ArrayList<>(minHeap);
        top10.sort(Collections.reverseOrder());
        return top10;
    }

    /**
     * @param p la publication à traiter
     * @param uf la structure Union-Find
     *
     * Traite une publication en ajoutant ses auteurs à l'Union-Find.
     */
    private static void processPublication(DblpPublicationGenerator.Publication p, UnionFind uf) { 

        // Publication sans auteur
        List<String> authors = p.authors;
        if (authors == null || authors.isEmpty()) {
            return;
        }

        // Noms d'auteur-rice-s répétés dans la même publication
        List<String> uniqueAuthors = authors.stream()
                                            .distinct()
                                            .collect(Collectors.toList());

        if (uniqueAuthors.size() == 1) {
            uf.add(uniqueAuthors.get(0));
            return;
        }

        String first = uniqueAuthors.get(0);
        for (int i = 1; i < authors.size(); i++) {
            uf.union(first, authors.get(i));
        }
    }

    private static void printStats(long pubCount, UnionFind uf, List<Integer> top10) { 
        /**
         * @param pubCount le nombre de publication
         * @param uf les communautés
         * @param top10 la liste contenant les tailles des 10 plus grandes communautés
         *
         * S'occupe d'afficher le résultat après le parsing
         */
        System.out.println("------ Exigence online : " + pubCount + "ème publication -----");
        System.out.println("Il y a " + uf.getCount() + " communautés.");
        System.out.println("Top 10 des plus grandes communautés");
        for (int i = 0; i < top10.size(); i++) {
            System.out.println((i+1) + ". " + top10.get(i) + " auteurs");
        }
    }

    private static List<Integer> getSizeOfCommunities(UnionFind uf) {
        Map<String, Integer> sizeOfCommunities = uf.sizeOfComm();
        List<Integer> res = new ArrayList<>(); 
        for (int community : sizeOfCommunities.values()) {
            res.add(community);
        }
        return res;
    }
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("""
                    Usage:
                    java -Xmx2g DblpParsingDemo <dblp.xml|dblp.xml.gz> <dblp.dtd> [--limit=1000000]

                    Exemple:
                    java -Xmx2g DblpParsingDemo dblp.xml.gz dblp.dtd --limit=500000
                    """);
            System.exit(2);
        }

        Path xmlPath = Paths.get(args[0]);
        Path dtdPath = Paths.get(args[1]);

        long limit = Long.MAX_VALUE; // optionnel: s'arrêter après N publications
        for (int i = 2; i < args.length; i++) {
            String a = args[i];
            if (a.startsWith("--limit=")) limit = Long.parseLong(a.substring("--limit=".length()));
        }

        if (!Files.exists(xmlPath)) throw new FileNotFoundException("XML introuvable: " + xmlPath);
        if (!Files.exists(dtdPath)) throw new FileNotFoundException("DTD introuvable: " + dtdPath);

        // --------------------------------------------------------------------
        // IMPORTANT : limites d'expansion d'entités XML
        // --------------------------------------------------------------------
        // DBLP utilise un DTD qui définit beaucoup d'entités.
        // Le parseur XML de Java impose par défaut une limite sur le nombre
        // d'expansions d'entités pour se protéger d'attaques (type "Billion Laughs").
        //
        // Sur DBLP (fichier légitime), on dépasse souvent la limite par défaut (p.ex. 2500),
        // ce qui déclenche une erreur du type:
        //   JAXP00010001: The parser has encountered more than "2500" entity expansions...
        //
        // Ici, comme on parse un fichier connu + un DTD local (pas de réseau),
        // on désactive ces limites pour éviter l'erreur.
        //
        // À ne pas faire pour des XML non fiables.
        // --------------------------------------------------------------------
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxParameterEntitySizeLimit", "0");

        System.out.println("XML: " + xmlPath);
        System.out.println("DTD: " + dtdPath);
        if (limit != Long.MAX_VALUE) System.out.println("Limit: " + limit);

        long pubCount = 0;

        // --------------------------------------------------------------------
        // On crée le générateur DBLP dans un try-with-resources :
        //   - le constructeur démarre un thread de parsing en arrière-plan ;
        //   - les publications "parsé(e)s" sont déposées dans une file (queue) ;
        //   - gen.nextPublication() consomme cette file au fur et à mesure.
        //
        // Le try-with-resources garantit que gen.close() est appelé à la fin,
        // ce qui permet d'arrêter proprement le thread de parsing et de libérer
        // les ressources.
        // --------------------------------------------------------------------
        try (DblpPublicationGenerator gen = new DblpPublicationGenerator(xmlPath, dtdPath, 256)) {
            // Boucle de consommation : on traite les publications une par une,
            // jusqu'à atteindre la limite (si fournie) ou la fin du fichier.

            UnionFind uf = new UnionFind();
            Map<String, Integer>  relation = new HashMap<>();

            while (pubCount < limit) {

                // nextPublication() renvoie :
                //   - Optional.of(pub) si une publication est disponible ;
                //   - Optional.empty() si on a atteint la fin du flux (EOF).
                //
                // Cela évite d'utiliser null et oblige à gérer explicitement le cas EOF.
                Optional<DblpPublicationGenerator.Publication> opt = gen.nextPublication();
                if (opt.isEmpty()) break; // EOF

                pubCount++;
                
                DblpPublicationGenerator.Publication p = opt.get();

                processPublication(opt.get(), uf);
                // Condition online
                if (pubCount % Math.pow(10, 5) == 0) {
                    printStats(pubCount, uf, getTop10(uf));
                }

                // Pour éviter des erreurs à la compilation après factorisation
                List<String> authors = p.authors;

                int k = authors.size();
                // autres auteurs (peut être vide si k == 1)
                List<String> others = (k > 1) ? authors.subList(1, k) : List.of();

                // Préparation pour la construction du graphe orienté
                if (k >= 2) {
                    String authorA = authors.get(0);

                    for (int i = 1; i < k; i++) {
                        String authorB = authors.get(i);

                        if (!authorB.equals(authorA)) {
                            String pair = authorA + "->" + authorB;

                            relation.merge(pair, 1, Integer::sum);
                        }
                    }
                }
            }

            Map<String, Integer> authorsToId = new HashMap<>();
            int idCount = 0;

            for (Map.Entry<String, Integer> entry : relation.entrySet()) {
                if (entry.getValue() >= 6) {
                    String[] parts = entry.getKey().split("->");
                    for (String name : parts) {
                        if (!authorsToId.containsKey(name)) {
                            authorsToId.put(name, idCount++);
                        }
                    }
                }
            }

            Digraph graph = new Digraph(idCount);
            for (Map.Entry<String, Integer> entry : relation.entrySet()) {
                if (entry.getValue() >= 6) {
                    String[] parts = entry.getKey().split("->");
                    int u = authorsToId.get(parts[0]);
                    int v = authorsToId.get(parts[1]);
                    graph.addEdge(u, v);
                }
            }

            // On inverse la Symbol Table pour retrouver le nom à partir de l'ID
            String[] nameById = new String[idCount];
            for (String name : authorsToId.keySet()) {
                nameById[authorsToId.get(name)] = name;
            }

            // Selon https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/KosarajuSharirSCC.html, c'est un ADT qui permet de calculer les composentes à connexité forte
            KosarajuSharirSCC scc = new KosarajuSharirSCC(graph);

            // Pour regrouper les auteurs par communauté
            Map<Integer, List<String>> components = new HashMap<>();
            for (int v = 0; v < graph.V(); v++) {
                int id = scc.id(v);
                components.computeIfAbsent(id, k -> new ArrayList<>()).add(nameById[v]);
            }

            System.out.println("Nombre de SCC détectées : " + scc.count());

            System.out.println("\n --- Test du plus court chemin ---");
            Random rand = new Random();
            int nbTests = scc.count(); // Nombre de paires à tester
            int cheminsTrouves = 0;
            System.out.println("\n--- Test de " + nbTests + " paires aléatoires ---");
            for (int i = 0; i < nbTests; i++) {
                int idA = rand.nextInt(idCount);
                int idB = rand.nextInt(idCount);

                int dist = BFS(graph, idA, idB);

                if (dist != -1) {
                    System.out.println("[SUCCÈS] " + nameById[idA] + " -> " + nameById[idB] + " : " + dist + " sauts");
                    cheminsTrouves++;
                }
            }
            System.out.println("Bilan : " + cheminsTrouves + "/" + nbTests + " chemins trouvés.");

            List<Integer> res = getSizeOfCommunities(uf);
            // Écriture dans un fichier CSV
            try (PrintWriter pw = new PrintWriter("communities.csv")) {
                pw.println("size");
                for (int size : res) {
                    pw.println(size);
                }
            }
        }
    }
}

