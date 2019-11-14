package innopolis.stc21.MA;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.*;

public class MyReflectionsMethodsTest {

    private class ParrentClass {

        public int anInt;
        public long aLong;
        private float aFloat;
        private double aDouble;
        private String string;


        public int getAnInt() {
            return anInt;
        }

        public long getaLong() {
            return aLong;
        }

        public float getaFloat() {
            return aFloat;
        }

        public double getaDouble() {
            return aDouble;
        }

        public String getString() {
            return string;
        }

        ParrentClass() {
            anInt = 3;
            aLong = 4L;
            aFloat = 5.5f;
            aDouble = 6.6;
            string = "Not null string";
        }
    }

    private class TestClass extends ParrentClass {
        public boolean aBoolean;
        public byte aByte;
        private char aChar;
        private short aShort;

        public boolean isaBoolean() {
            return aBoolean;
        }

        public byte getaByte() {
            return aByte;
        }

        public char getaChar() {
            return aChar;
        }

        public short getaShort() {
            return aShort;
        }

        TestClass() {
            aBoolean = true;
            aByte = 1;
            aChar = '!';
            aShort = 2;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cleanupObjectFail() {
        MyReflectionsMethods.cleanup(new TestClass(), Set.of("absented name"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cleanupMapFail() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("key0", "value0");

        MyReflectionsMethods.cleanup(hashMap, Set.of("absented key"), null);
    }

    @Test
    public void cleanupObject() {
        TestClass testClass = new TestClass();
        Set<String> allFields = new LinkedHashSet<>() {{
            add("aBoolean");
            add("aByte");
            add("aChar");
            add("aShort");
            add("anInt");
            add("aLong");
            add("aFloat");
            add("aDouble");
            add("string");
        }};
        Set<String> emptySet = Collections.EMPTY_SET;

        MyReflectionsMethods.cleanup(testClass, null, null);
        MyReflectionsMethods.cleanup(testClass, emptySet, emptySet);

        assertTrue(testClass.isaBoolean());
        assertEquals(testClass.getaByte(), (byte) 1);
        assertEquals(testClass.getaChar(), '!');
        assertEquals(testClass.getaShort(), (short) 2);
        assertEquals(testClass.getAnInt(), 3);
        assertEquals(testClass.getaLong(), 4L);
        assertEquals(testClass.getaFloat(), 5.5F, 0.0f);
        assertEquals(testClass.getaDouble(), 6.6, 0.0);
        assertEquals(testClass.getString(), "Not null string");

        //Output fields
        System.out.println("Before Cleanup:");
        MyReflectionsMethods.cleanup(testClass, emptySet, allFields);

        PrintStream sysOut = System.out;
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baOut);
        System.setOut(out);

        MyReflectionsMethods.cleanup(testClass, emptySet, allFields);

        String outputSample = new String(baOut.toByteArray());
        baOut.reset();
        String defaultSample = "aBoolean: true" + System.lineSeparator() +
                "aByte: 1" + System.lineSeparator() +
                "aChar: !" + System.lineSeparator() +
                "aShort: 2" + System.lineSeparator() +
                "anInt: 3" + System.lineSeparator() +
                "aLong: 4" + System.lineSeparator() +
                "aFloat: 5.5" + System.lineSeparator() +
                "aDouble: 6.6" + System.lineSeparator() +
                "string: Not null string" + System.lineSeparator();
        assertEquals(defaultSample, outputSample);

        //Cleanup all fields
        MyReflectionsMethods.cleanup(testClass, allFields, emptySet);

        assertFalse(testClass.isaBoolean());
        assertEquals(testClass.getaByte(), (byte) 0);
        assertEquals(testClass.getaChar(), '\u0000');
        assertEquals(testClass.getaShort(), (short) 0);
        assertEquals(testClass.getAnInt(), 0);
        assertEquals(testClass.getaLong(), 0L);
        assertEquals(testClass.getaFloat(), 0.0F, 0.0f);
        assertEquals(testClass.getaDouble(), 0.0, 0.0);
        assertNull(testClass.getString());


        MyReflectionsMethods.cleanup(testClass, emptySet, allFields);
        outputSample = new String(baOut.toByteArray());
        String cleanSample = "aBoolean: false" + System.lineSeparator() +
                "aByte: 0" + System.lineSeparator() +
                "aChar: " + '\u0000' + System.lineSeparator() +
                "aShort: 0" + System.lineSeparator() +
                "anInt: 0" + System.lineSeparator() +
                "aLong: 0" + System.lineSeparator() +
                "aFloat: 0.0" + System.lineSeparator() +
                "aDouble: 0.0" + System.lineSeparator() +
                "string: null" + System.lineSeparator();
        assertEquals(cleanSample, outputSample);

        System.setOut(sysOut);
        try {
            baOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Output cleanup fields
        System.out.println("\nAfter Cleanup:");
        MyReflectionsMethods.cleanup(testClass, emptySet, allFields);
    }

    @Test()
    public void cleanupMap() {
        Map<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            hashMap.put("key" + String.valueOf(i), "value" + String.valueOf(i));
        }

        Set<String> setToOutput = new LinkedHashSet<>() {{
            add("key0");
            add("key4");
            add("key7");
        }};

        PrintStream sysOut = System.out;
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baOut);
        System.setOut(out);

        MyReflectionsMethods.cleanup(hashMap, Set.of("key1", "key5", "key8"), setToOutput);
        assertEquals(7, hashMap.size());
        assertEquals("value0", hashMap.get("key0"));
        assertNull(hashMap.get("key1"));
        assertEquals("value2", hashMap.get("key2"));


        String outputSample = new String(baOut.toByteArray());
        baOut.reset();
        String defaultSample = "key0=value0" + System.lineSeparator() +
                "key4=value4" + System.lineSeparator() +
                "key7=value7" + System.lineSeparator();
        assertEquals(defaultSample, outputSample);
        System.setOut(sysOut);
        try {
            baOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MyReflectionsMethods.cleanup(hashMap, null, setToOutput);

    }

}

