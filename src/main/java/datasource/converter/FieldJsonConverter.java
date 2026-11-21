package datasource.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class FieldJsonConverter implements AttributeConverter<int[][], String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(int[][] matrix) {
        try { return mapper.writeValueAsString(matrix); }
        catch (JsonProcessingException e) { throw new RuntimeException(e); }
    }

    @Override
    public int[][] convertToEntityAttribute(String json) {
        try { return mapper.readValue(json, int[][].class); }
        catch (JsonProcessingException e) { throw new RuntimeException(e); }
    }
}
