package innopolis.stc21.MA;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class MyReflectionsMethods {


    /**
     * Method for cleanup specified fields and thereafter output another specified fields
     */
    public static void cleanup(Object object, Set<String> fieldsToCleanup, Set<String> fieldsToOutput) {
        if (fieldsToCleanup == null) {
            fieldsToCleanup = Collections.emptySet();
        }
        if (fieldsToOutput == null) {
            fieldsToOutput = Collections.emptySet();
        }
        if (object instanceof Map) {
            Map map = (Map) object;
            cleanupMap(map, fieldsToCleanup, fieldsToOutput);
        } else {
            cleanupObject(object, fieldsToCleanup, fieldsToOutput);
        }
    }

    private static void cleanupObject(Object object, Set<String> fieldsToCleanup, Set<String> fieldsToOutput) {
        List<Field> fields = getAllFields(object.getClass());
        Set<String> fieldsToChecking = new HashSet<>() {{
            addAll(fieldsToCleanup);
            addAll(fieldsToOutput);
        }};
        if (!fields.stream().map(Field::getName).collect(Collectors.toSet()).containsAll(fieldsToChecking)) {
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
        if (!keysToChecking.stream().allMatch(map::containsKey)
                || keysToCleanup.stream().anyMatch(keysToOutput::contains)) {
            throw new IllegalArgumentException();
        }
        keysToCleanup.forEach(map::remove);
        keysToOutput.forEach(f -> System.out.println(String.format("%s=%s", f, map.get(f))));
    }

    private static List<Field> getAllFields(Class clazz) {
        List<Field> fieldsList = new ArrayList<>();
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
            System.out.println("Сaught IllegalAccessException");
        }
    }

    private static void fieldToOutput(Field field, Object object) {
        field.setAccessible(true);
        try {
            System.out.println(String.format("%s: %s", field.getName(), String.valueOf(field.get(object))));
        } catch (IllegalAccessException e) {
            System.out.println("Сaught IllegalAccessException");
        }
    }
}
