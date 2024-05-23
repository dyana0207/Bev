package util;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class JacksonHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    /**
     * Deserializes a list of objects from JSON.
     *
     * @param in the input stream from which JSON data will be read
     * @param elementClass represents the class of the elements
     * @return the list of objects deserialized from JSON
     * @param <T> the type of the list elements
     * @throws IOException if any I/O error occurs
     */
    public static <T> List<T> readList(InputStream in, Class<T> elementClass) throws IOException {
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
        return MAPPER.readValue(in, type);
    }

    /**
     * Serializes a list of objects to JSON.
     *
     * @param out the output stream to which JSON data will be written
     * @param list the list of objects to be serialized
     * @param <T> the type of the list elements
     * @throws IOException if any I/O error occurs
     */
    public static <T> void writeList(OutputStream out, List<T> list) throws IOException {
        MAPPER.writeValue(out, list);
    }
}
