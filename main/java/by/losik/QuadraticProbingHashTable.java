package by.losik;

import java.util.Scanner;

public class QuadraticProbingHashTable {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    static class Entry {
        String K; // Ключевое слово или ID
        boolean C; // Флаг коллизии
        boolean U; // Флаг "занято"
        boolean T; // Терминальный флажок
        boolean L; // Флаг связи (true - указатель, false - данные)
        boolean D; // Флаг вычеркивания
        int Po;    // Указатель области переполнения
        Object Pi; // Данные или указатель области данных

        Entry() {
            U = false;
            D = false;
        }
    }

    private Entry[] table;
    private int size;
    private int capacity;

    public QuadraticProbingHashTable() {
        this(DEFAULT_CAPACITY);
    }

    public QuadraticProbingHashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new Entry[initialCapacity];
        for (int i = 0; i < initialCapacity; i++) {
            table[i] = new Entry();
        }
    }

    public void insert(String key, Object data) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        if ((double) size / capacity >= LOAD_FACTOR) {
            rehash();
        }

        int index = hash(key);
        int originalIndex = index;
        int i = 1;

        // Поиск места для вставки
        while (table[index].U && !table[index].D) {
            if (table[index].K.equals(key)) {
                table[index].Pi = data;
                return;
            }
            table[index].C = true;
            index = (originalIndex + i * i) % capacity;
            i++;
        }

        // Вставляем новый элемент
        table[index].K = key;
        table[index].U = true;
        table[index].D = false;
        table[index].T = true;
        table[index].L = false;
        table[index].Pi = data;
        table[index].Po = -1;
        size++;
    }

    public Object search(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        int index = hash(key);
        int originalIndex = index;
        int i = 1;

        while (table[index].U) {
            if (!table[index].D && table[index].K.equals(key)) {
                return table[index].Pi;
            }
            index = (originalIndex + i * i) % capacity;
            i++;
            // Если мы вернулись к исходному индексу, значит, ключ не найден
            if (index == originalIndex) break;
        }

        return null; // Ключ не найден
    }

    public void delete(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        int index = hash(key);
        int originalIndex = index;
        int i = 1;

        while (table[index].U) {
            if (!table[index].D && table[index].K.equals(key)) {
                table[index].D = true;
                size--;
                return;
            }
            index = (originalIndex + i * i) % capacity;
            i++;
            if (index == originalIndex) break;
        }
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    private void rehash() {
        int newCapacity = capacity * 2;
        Entry[] oldTable = table;
        table = new Entry[newCapacity];
        capacity = newCapacity;
        size = 0;

        for (int i = 0; i < newCapacity; i++) {
            table[i] = new Entry();
        }

        for (Entry entry : oldTable) {
            if (entry.U && !entry.D) {
                insert(entry.K, entry.Pi);
            }
        }
    }

    public Entry getEntry(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        int index = hash(key);
        int originalIndex = index;
        int i = 1;
        while (table[index].U) {
            if (!table[index].D && table[index].K.equals(key)) {
                return table[index];
            }
            index = (originalIndex + i * i) % capacity;
            i++;
            if (index == originalIndex) break;
        }
        return null;
    }

    public void printTable() {
        System.out.println("\nHash Table Contents:");
        System.out.println("Index | K     | U | C | T | L | D | Po  | Pi");
        for (int i = 0; i < capacity; i++) {
            Entry entry = table[i];
            System.out.printf("%5d | %-5s | %s | %s | %s | %s | %s | %3d | %s%n",
                    i,
                    entry.K != null ? entry.K : "null",
                    entry.U ? "T" : "F",
                    entry.C ? "T" : "F",
                    entry.T ? "T" : "F",
                    entry.L ? "T" : "F",
                    entry.D ? "T" : "F",
                    entry.Po,
                    entry.Pi != null ? entry.Pi.toString() : "null");
        }
    }

    public static void main(String[] args) {
        QuadraticProbingHashTable table = new QuadraticProbingHashTable(8);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Hash Table CRUD Operations Console");
        System.out.println("Available commands:");
        System.out.println("  insert <key> <value> - Add/update an entry");
        System.out.println("  search <key>         - Search for an entry");
        System.out.println("  delete <key>         - Delete an entry");
        System.out.println("  print                - Display the hash table");
        System.out.println("  exit                 - Quit the program");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 3);
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "insert" -> {
                        if (parts.length < 3) {
                            System.out.println("Usage: insert <key> <value>");
                            break;
                        }
                        table.insert(parts[1], parts[2]);
                        System.out.println("Inserted/updated entry with key '" + parts[1] + "'");
                    }
                    case "search" -> {
                        if (parts.length < 2) {
                            System.out.println("Usage: search <key>");
                            break;
                        }
                        Object result = table.search(parts[1]);
                        if (result != null) {
                            System.out.println("Found: " + result);
                        } else {
                            System.out.println("Key '" + parts[1] + "' not found");
                        }
                    }
                    case "delete" -> {
                        if (parts.length < 2) {
                            System.out.println("Usage: delete <key>");
                            break;
                        }
                        table.delete(parts[1]);
                        System.out.println("Deleted entry with key '" + parts[1] + "'");
                    }
                    case "print" -> table.printTable();
                    case "exit" -> {
                        System.out.println("Exiting program...");
                        scanner.close();
                        return;
                    }
                    default -> {
                        System.out.println("Unknown command. Available commands:");
                        System.out.println("  insert <key> <value>");
                        System.out.println("  search <key>");
                        System.out.println("  delete <key>");
                        System.out.println("  print");
                        System.out.println("  exit");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}