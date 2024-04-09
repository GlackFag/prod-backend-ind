package com.glackfag.travelgentle.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glackfag.travelgentle.models.Address;

import java.util.NoSuchElementException;
import java.util.Objects;

public class JsonParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String retrieveField(String json, int index) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(json);

        if (node.size() <= index)
            throw new IndexOutOfBoundsException("Index " + index + " is out of json elements. Json: " + json);

        return node.get(index).toString();
    }


    public static String retrieveField(String json, String fieldName) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(json);

        if (!node.has(fieldName))
            throw new NoSuchElementException("No field '" + fieldName + "' in provided json: " + json);

        return node.get(fieldName).toString();
    }

    /**
     * @param json - JSON полученный через GET запрос на nominatim.openstreetmap.org/reverse
     * @return Address полученный из ответа
     * @throws JsonProcessingException если в предоставленном JSON нет элемента "address"
     */
    public static Address retrieveAddress(String json) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(retrieveField(json, "address"));

        String country = Objects.toString(node.get("country"));
        String city = Objects.toString(node.get("city"));
        String street = Objects.toString(node.get("road"));
        String building = Objects.toString(node.get("house_number"));

        return new Address(country.replaceAll("\"", ""),
                city.replaceAll("\"", ""),
                street.replaceAll("\"", ""),
                building.replaceAll("\"", ""));
    }
}
