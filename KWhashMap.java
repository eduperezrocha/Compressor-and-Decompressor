import java.util.LinkedList;
 /*
 * 
 * Authors: Eduardo Perez Rocha
 * 
 */
public class KWhashMap<K, V> {
    private class Entry<K, V> {
        private final K key;
        private V value;
    

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private LinkedList<Entry<K, V>>[] table;
    private int numKeys;
    private static final int CAPACITY = 10;  // Default initial capacity
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;
    private int rehashCount = 0;

    public KWhashMap(int capacity) {
        table = new LinkedList[capacity];
        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<Entry<K, V>>();
        }
    }

    // Default constructor
    public KWhashMap() {
        this(CAPACITY); // Call the other constructor with the default capacity
    }


    private int hashIndex(K key) {
        int index = key.hashCode() % table.length;
        if (index < 0) {
            index += table.length;
        }
        return index; // Return the corrected index
    }
    

    public V put(K key, V value) {
        int index = hashIndex(key);

        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }

        for (Entry<K, V> entry : table[index]) {
            if (entry.getKey().equals(key)) {
                return entry.setValue(value);
            }
        }

        table[index].add(new Entry<>(key, value));
        numKeys++;

        if (numKeys > (LOAD_FACTOR_THRESHOLD * table.length)) {
            rehash();
        }

        return null;
    }

    public boolean containsKey(K key) {
        int index = hashIndex(key);

        if (table[index] != null) {
            for (Entry<K, V> entry : table[index]) {
                if (entry.getKey().equals(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    public V get(K key) {
        int index = hashIndex(key);

        if (table[index] != null) {
            for (Entry<K, V> entry : table[index]) {
                if (entry.getKey().equals(key)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    private void rehash() {
        LinkedList<Entry<K, V>>[] oldTable = table;
        table = new LinkedList[oldTable.length * 2];
        numKeys = 0;
        rehashCount++; 

        for (LinkedList<Entry<K, V>> bucket : oldTable) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public int getRehashCount() {
        return rehashCount;
    }
}
