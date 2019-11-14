package innopolis.stc21.MA;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class MyReflectionsMethods {


    public static void cleanup(Object object, Set<String> fieldsToCleanup, Set<String> fieldsToOutput) {
        if (fieldsToCleanup == null) {
            fieldsToCleanup = Collections.EMPTY_SET;
        }
        if (fieldsToOutput == null) {
            fieldsToOutput = Collections.EMPTY_SET;
        }
        if (object instanceof Map) {
            Map map = (Map<Object, Object>) object;
            cleanupMap(map, fieldsToCleanup, fieldsToOutput);
        } else {
            cleanupObject(object, fieldsToCleanup, fieldsToOutput);
        }
    }

    private static void cleanupObject(Object object, Set<String> fieldsToCleanup, Set<String> fieldsToOutput) {
        ArrayList<Field> fields = getAllFields(object.getClass());

        Set<String> fieldsToChecking = new HashSet<>() {{
            addAll(fieldsToCleanup);
            addAll(fieldsToOutput);
        }};
        Set<String> fieldsName = fields.stream().map(x -> x.getName()).collect(Collectors.toSet());
        if (!fieldsToChecking.stream().allMatch(f -> fieldsName.contains(f))) {
            throw new IllegalArgumentException();
        }
        fields.stream().filter(f -> fieldsToCleanup.contains(f.getName())).forEach(f -> fieldToCleanup(f, object));
        fields.stream().filter(f -> fieldsToOutput.contains(f.getName())).forEachOrdered(f -> fieldToOutput(f, object));

    }

    private static void cleanupMap(Map<Object, Object> map, Set<String> keysToCleanup, Set<String> keysToOutput) {

        Set<String> keysToChecking = new HashSet<>() {{
            addAll(keysToCleanup);
            addAll(keysToOutput);
        }};
        if (!keysToChecking.stream().allMatch(k -> map.containsKey(k))
                || keysToCleanup.stream().anyMatch(f -> keysToOutput.contains(f))) {
            throw new IllegalArgumentException();
        }
        keysToCleanup.stream().forEach(f -> map.remove(f));
        keysToOutput.stream().sequential().forEachOrdered(f -> System.out.println(String.format("%s=%s", f, map.get(f))));


    }

    private static ArrayList<Field> getAllFields(Class clazz) {
        ArrayList<Field> fieldsList = new ArrayList<>();
        while (clazz != null) {
            Collections.addAll(fieldsList, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return fieldsList;
    }

    private static void fieldToCleanup(Field field, Object object) {
        field.setAccessible(true);
        try {
            switch (field.getGenericType().getTypeName()) {
                case "boolean":
                    field.setBoolean(object, false);
                    break;
                case "byte":
                    field.setByte(object, (byte) 0);
                    break;
                case "char":
                    field.setChar(object, '\u0000');
                    break;
                case "short":
                    field.setShort(object, (short) 0);
                    break;
                case "int":
                    field.setInt(object, 0);
                    break;
                case "long":
                    field.setLong(object, 0L);
                    break;
                case "float":
                    field.setFloat(object, 0.0F);
                    break;
                case "double":
                    field.setDouble(object, 0.0);
                    break;
                default:
                    field.set(object, null);
                    break;
            }
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
            // Nothing
        }

    }

    private static void fieldToOutput(Field field, Object object) {
        field.setAccessible(true);
        try {
            switch (field.getGenericType().getTypeName()) {
                case "boolean":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((boolean) field.get(object))));
                    break;
                case "byte":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((byte) field.get(object))));
                    break;
                case "char":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((char) field.get(object))));
                    break;
                case "short":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((short) field.get(object))));
                    break;
                case "int":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((int) field.get(object))));
                    break;
                case "long":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((long) field.get(object))));
                    break;
                case "float":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((float) field.get(object))));
                    break;
                case "double":
                    System.out.println(String.format("%s: %s", field.getName(), String.valueOf((double) field.get(object))));
                    break;
                default:
                    Object fieldsValue = field.get(object);
                    System.out.println(String.format("%s: %s", field.getName(), fieldsValue == null ? "null" : fieldsValue.toString()));
                    break;
            }
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
            // Nothing
        }
    }


    // todo джавадоки заполнить
    // todo нужно обрабатыват ситуацию когда во входных сетах есть одинаковые строки?
    // todo сделать проверку входных коллекций на null
}
