package by.losik;

public class App {
    public static void main(String[] args) {
        HashTableWithQuadraticProbing<String, Integer> table = new HashTableWithQuadraticProbing<>(4);

        table.put("one", 1);
        table.put("two", 2);
        table.put("three", 3);
        table.put("four", 4);
        table.put("four", 7);
        table.put("five", 5); // Вызовет рехеширование
        table.put("six", 6);
        table.put("seven", 7);
        table.put("eight", 85);
        table.put("nine", 9);

        System.out.println("Size: " + table.size());
        System.out.println("Get 'three': " + table.get("three"));
        System.out.println("Get 'two': " + table.get("two"));

        table.remove("two");
        System.out.println("Contains 'two' after removal: " + table.containsKey("two"));

        table.put("two", 22);
        System.out.println("Get new 'two': " + table.get("two"));
        System.out.println("The table:\n"+table);

        table.remove("eight");
        System.out.println(table);
    }
}
