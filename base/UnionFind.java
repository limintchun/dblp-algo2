import java.util.Map;
import java.util.HashMap;

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

    public void add(String x) {
        if (!parent.containsKey(x)) {
            parent.put(x,x);
            size.put(x, 1);
            height.put(x, 0);
            maxSize = Math.max(maxSize, 1);
            count++;
        }
    }

    // Implémentation compression de chemin
    public String find(String x) {
        String root = x;
        while (!root.equals(parent.get(root))) {
            root = parent.get(root);
        }
        while (x != root) {
            String newx = parent.get(x);
            parent.put(x, root);
            x = newx;
        }
        return root;
    }

    // Implémentation de l'union rapide pondérée
    public void union(String x, String y) {
        add(x);
        add(y);

        // Recherche des racines
        String rx = find(x);
        String ry = find(y);

        // rx et ry se trouve dans le même groupe
        if (rx.equals(ry)) return;

        // Fusion du plus petit groupe au plus grand
        if (height.get(rx) < height.get(ry)) {
            parent.put(rx, ry);
            size.put(ry, size.get(rx) + size.get(ry));
        }

        // L'inverse
        else if (height.get(rx) > height.get(ry)) {
            parent.put(ry,rx);
            size.put(rx, size.get(rx) + size.get(ry));
        }
        else {
            parent.put(ry, rx);
            height.put(rx, height.get(rx) + 1);
            size.put(rx, size.get(rx) + size.get(ry));
        }

        size.put(rx, size.get(rx) + size.get(ry));
        maxSize = Math.max(maxSize, size.get(rx.equals(parent.get(rx)) ? rx : ry));
        count--;
    }

    public boolean connected(String x, String y) {
        return find(x).equals(find(y));
    }

    public int getCount() {
        return count;
    }

    public int getMaxSize() {
        return maxSize;
    }
}

