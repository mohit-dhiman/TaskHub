package md.java.taskhub.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // NOTE: Creating the ObjectMapper so that the Java 8 Time library get properly converted to Json
    public static ObjectMapper objectMapperWithTimeModuleSupport() {
        return objectMapper;
    }
}
