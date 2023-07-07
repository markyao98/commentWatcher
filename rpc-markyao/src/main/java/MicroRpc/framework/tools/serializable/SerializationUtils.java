package MicroRpc.framework.tools.serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SerializationUtils {

    public static <T> byte[] serializeObject(T object) {
        ObjectMapper objectMapper=new ObjectMapper();
        byte[] bytes=null;
        try {
            bytes = objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static <T>T deserializeObject(byte[] bytes,Class<T>clazz) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
