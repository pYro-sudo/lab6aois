package by.losik;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class QuadraticProbingHashTableTest {
    private QuadraticProbingHashTable hashTable;

    @BeforeEach
    void setUp() {
        hashTable = new QuadraticProbingHashTable(8);
    }

    @Test
    void testInsertAndSearch() {
        hashTable.insert("key1", "value1");
        hashTable.insert("key2", "value2");
        assertEquals("value1", hashTable.search("key1"));
        assertEquals("value2", hashTable.search("key2"));
        assertNull(hashTable.search("key3"));
    }

    @Test
    void testInsertDuplicate() {
        hashTable.insert("key1", "value1");
        hashTable.insert("key1", "newValue");
        assertEquals("newValue", hashTable.search("key1"));
    }

    @Test
    void testDelete() {
        hashTable.insert("key1", "value1");
        hashTable.delete("key1");
        assertNull(hashTable.search("key1"));
    }

    @Test
    void testDeleteNonExistent() {
        hashTable.insert("key1", "value1");
        hashTable.delete("key2");
        assertEquals("value1", hashTable.search("key1"));
    }

    @Test
    void testCollisionHandling() {
        hashTable.insert("a", "val1");
        hashTable.insert("i", "val2");
        hashTable.insert("q", "val3");
        assertEquals("val1", hashTable.search("a"));
        assertEquals("val2", hashTable.search("i"));
        assertEquals("val3", hashTable.search("q"));
    }

    @Test
    void testRehashing() {
        for (int i = 0; i < 10; i++) {
            hashTable.insert("key" + i, "value" + i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals("value" + i, hashTable.search("key" + i));
        }
    }

    @Test
    void testNullKey() {
        assertThrows(IllegalArgumentException.class, () -> hashTable.insert(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> hashTable.search(null));
        assertThrows(IllegalArgumentException.class, () -> hashTable.delete(null));
    }

    @Test
    void testFlags() {
        hashTable.insert("key1", "value1");
        QuadraticProbingHashTable.Entry entry = hashTable.getEntry("key1");
        assertTrue(entry.U);
        assertFalse(entry.D);
        assertTrue(entry.T);
        assertFalse(entry.L);
    }

    @Test
    void testDeletedFlag() {
        hashTable.insert("key1", "value1");
        hashTable.delete("key1");
        QuadraticProbingHashTable.Entry entry = hashTable.getEntry("key1");
        assertNull(entry);
    }

    @Test
    void testCollisionFlag() {
        hashTable.insert("a", "val1");
        hashTable.insert("i", "val2");
        QuadraticProbingHashTable.Entry entry = hashTable.getEntry("a");
        assertTrue(entry.C);
    }

    @Test
    void testPrintTable() {
        QuadraticProbingHashTable table = new QuadraticProbingHashTable(4);
        table.insert("a", 1);
        table.insert("b", 2);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        table.printTable();

        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("Hash Table Contents"));
        assertTrue(output.contains("Index | K"));
        assertTrue(output.contains("a"));
        assertTrue(output.contains("b"));
    }
    @Test
    void testMainWithInsertAndPrint() {
        String input = "insert apple fruit\nprint\nexit\n";
        String expectedOutput = "Inserted/updated entry with key 'apple'.*Hash Table Contents:.*apple.*";

        assertMainOutputMatches(input, expectedOutput);
    }

    @Test
    void testMainWithSearch() {
        String input = "insert apple fruit\nsearch apple\nexit\n";
        String expectedOutput = "Inserted/updated entry with key 'apple'.*Found: fruit";

        assertMainOutputMatches(input, expectedOutput);
    }

    @Test
    void testMainWithDelete() {
        String input = "insert apple fruit\ndelete apple\nsearch apple\nexit\n";
        String expectedOutput = "Inserted/updated entry with key 'apple'.*Deleted entry with key 'apple'.*Key 'apple' not found";

        assertMainOutputMatches(input, expectedOutput);
    }

    @Test
    void testMainWithInvalidCommand() {
        String input = "invalid command\nexit\n";
        String expectedOutput = "Unknown command.*";

        assertMainOutputMatches(input, expectedOutput);
    }

    @Test
    void testMainWithMissingArguments() {
        String input = "insert apple\nsearch\nexit\n";
        String expectedOutput = "Usage: insert <key> <value>.*Usage: search <key>.*";

        assertMainOutputMatches(input, expectedOutput);
    }

    private void assertMainOutputMatches(String input, String expectedPattern) {
        // Save original System.in and System.out
        final ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        final ByteArrayOutputStream testOut = new ByteArrayOutputStream();

        System.setIn(testIn);
        System.setOut(new PrintStream(testOut));

        try {
            // Run the main method
            QuadraticProbingHashTable.main(new String[]{});

            // Get the output and normalize line endings
            String output = testOut.toString().replace("\r\n", "\n");

            // Verify the output contains expected pattern
            assertTrue(output.matches("(?s).*" + expectedPattern + ".*"),
                    "Output did not match pattern. Actual output:\n" + output);
        } finally {
            // Restore original System.in and System.out
            System.setIn(System.in);
            System.setOut(System.out);
        }
    }
}
