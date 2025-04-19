package by.losik;

public class HashTableWithQuadraticProbing<K, V> {
    private static final double LOAD_FACTOR = 0.75;
    private static final int DEFAULT_CAPACITY = 4;

    private Entry<K, V>[] table;
    private int size;
    private int threshold;

    public int getThreshold() {
        return this.threshold;
    }

    private static class Entry<K, V> {
        K key;
        V value;
        boolean deleted;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.deleted = false;
        }
    }

    @SuppressWarnings("unchecked")
    public HashTableWithQuadraticProbing(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.table = (Entry<K, V>[]) new Entry[capacity];
        this.threshold = (int) (capacity * LOAD_FACTOR);
    }

    public HashTableWithQuadraticProbing() {
        this(DEFAULT_CAPACITY);
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        if (size >= threshold) {
            resize();
        }

        int index = findSlotForPut(key);

        if (table[index] == null || table[index].deleted) {
            size++;
        } else if (!table[index].key.equals(key)) {
            throw new IllegalStateException("Invalid slot found");
        }

        table[index] = new Entry<>(key, value);
    }

    private int findSlotForPut(K key) {
        int index = hash(key);
        int i = 1;
        int firstDeleted = -1;

        while (true) {
            if (table[index] == null) {
                return firstDeleted != -1 ? firstDeleted : index;
            }

            if (table[index].deleted && firstDeleted == -1) {
                firstDeleted = index;
            } else if (!table[index].deleted && table[index].key.equals(key)) {
                return index;
            }

            index = (index + i * i) % table.length;
            i++;

            if (i > table.length) {
                if (firstDeleted != -1) {
                    return firstDeleted;
                }
                resize();
                index = hash(key);
                i = 1;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = (Entry<K, V>[]) new Entry[nextCapacity()];
        threshold = (int) (table.length * LOAD_FACTOR);
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            if (entry != null && !entry.deleted) {
                put(entry.key, entry.value);
            }
        }
    }

    int nextCapacity() {
        return table.length * 2;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        int index = hash(key);
        int i = 1;

        while (table[index] != null) {
            if (!table[index].deleted && table[index].key.equals(key)) {
                return table[index].value;
            }

            index = (index + i * i) % table.length;
            i++;

            if (i > table.length) {
                break;
            }
        }

        return null;
    }

    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        int index = hash(key);
        int i = 1;

        while (table[index] != null) {
            if (!table[index].deleted && table[index].key.equals(key)) {
                V value = table[index].value;
                table[index].deleted = true;
                size--;
                return value;
            }

            index = (index + i * i) % table.length;
            i++;

            if (i > table.length) {
                break;
            }
        }

        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return table.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Entry<K, V> entry : table) {
            if (entry != null && !entry.deleted) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.key).append("=").append(entry.value);
                first = false;
            }
        }

        sb.append("}");
        return sb.toString();
    }
}