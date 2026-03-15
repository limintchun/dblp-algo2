import java.util.Map;
import java.util.HashMap;

/**
 * Structure Union-Find avec compression de chemin et union rapide pondérée
 * Permet de gérer des communauté d'auteurs de manière efficace.
 */
public class UnionFind {

    private Map<String, String> parent;
    private Map<String, Integer> size;
    private int count;
    private int maxSize = 0;
    private Map<String, Integer> height;

    // constructureur
    public UnionFind() {
        parent = new HashMap<>();
        size = new HashMap<>();
        count = 0;
        height  = new HashMap<>();
    }

    /**
     * @param x le nom de l'auteur à ajouter
     *
     * Ajoute un noeud s'il existe pas encore.
     */
    public void add(String x) {
        if (!parent.containsKey(x)) {
            parent.put(x,x);
            size.put(x, 1);
            height.put(x, 0);
            maxSize = Math.max(maxSize, 1);
            count++;
        }
    }

    /**
     * @param x le nom de l'auteur
     * @return la racine de la communauté de x
     */
    public String find(String x) {
        String root = x;
        while (!root.equals(parent.get(root))) {
            root = parent.get(root);
        }
        while (!x.equals(root)) {
            String newx = parent.get(x);
            parent.put(x, root);
            x = newx;
        }
        return root;
    }

    /**
     * @param x premier auteur
     * @param y deuxième auteur
     *
     * Fusionne les communauté de x et y par union rapide pondérée
     */
    public void union(String x, String y) {
        add(x);
        add(y);

        // Recherche des racines
        String rx = find(x);
        String ry = find(y);

        // rx et ry se trouve dans le même groupe
        if (rx.equals(ry)) return;

        String newRoot;

        // Fusion du plus petit groupe au plus grand
        if (height.get(rx) < height.get(ry)) {
            parent.put(rx, ry);
            size.put(ry, size.get(rx) + size.get(ry));
            newRoot = ry;
        }

        // L'inverse
        else if (height.get(rx) > height.get(ry)) {
            parent.put(ry,rx);
            size.put(rx, size.get(rx) + size.get(ry));
            newRoot = rx;
        }
        else {
            parent.put(ry, rx);
            height.put(rx, height.get(rx) + 1);
            size.put(rx, size.get(rx) + size.get(ry));
            newRoot = rx;
        }

        maxSize = Math.max(maxSize, size.get(newRoot));
        count--;
    }

    /**
     * @param x premier auteur
     * @param y deuxième auteur
     * @return true si x et y sont connectés
     *
     * Vérifie si x et y appartiennent à la même communauté.
     */
    public boolean connected(String x, String y) {
        return find(x).equals(find(y));
    }

    /**
     * @return le nombre de communauté distinctes
     */
    public int getCount() {
        return count;
    }

    /**
     * @return la taille de la plus grande communauté
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @return une map associant chaque racine à la taille de sa communauté
     *
     */
    public Map<String, Integer> sizeOfComm() {
        Map<String, Integer> result = new HashMap<>();

        for (String node : parent.keySet()){
            String root = this.find(node);
            result.merge(root, 1, Integer::sum);
        }
        return result;
    }
}

