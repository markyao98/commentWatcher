package MicroRpc.framework.tools.beanutils;

import java.lang.reflect.Field;
import java.util.Map;

public class MapToObjectConverter {

    public static <T> T convert(Map<String, Object> map, Class<T> objectType) throws IllegalAccessException, InstantiationException {
        T object = objectType.newInstance();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            try {
                Field field = objectType.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, value);
            } catch (NoSuchFieldException e) {
                // Ignore fields that don't exist in the object
            }
        }

        return object;
    }
}
