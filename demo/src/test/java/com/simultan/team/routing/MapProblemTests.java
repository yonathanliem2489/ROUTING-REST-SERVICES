package com.simultan.team.routing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapProblemTests extends MapProblemUtils {

  private ObjectMapper mapper = new ObjectMapper();

  @Test
  void levelOne() throws JsonProcessingException {
    String jsonString = "[\n"
        + "  {\n"
        + "    \"id\": \"1\",\n"
        + "    \"name\": \"Regular\",\n"
        + "    \"price\": {\n"
        + "        \"finalPrice\": 62000\n"
        + "    },\n"
        + "    \"facilities\": [\n"
        + "      {\n"
        + "        \"name\": \"Full AC\",\n"
        + "        \"id\": \"ac\"\n"
        + "      }\n"
        + "    ]\n"
        + "  },\n"
        + "  {\n"
        + "    \"id\": \"2\",\n"
        + "    \"name\": \"Express\",\n"
        + "    \"price\": {\n"
        + "        \"finalPrice\": 80000\n"
        + "    },\n"
        + "    \"facilities\": [\n"
        + "      {\n"
        + "        \"name\": \"Full AC\",\n"
        + "        \"id\": \"ac\"\n"
        + "      },\n"
        + "      {\n"
        + "        \"name\": \"VIP Lounge\",\n"
        + "        \"id\": \"vip-lounge\"\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "]\n";

    String mappingSpecsString = "{\n"
        + "  \"inventoryId\": \"id\",\n"
        + "  \"price\": \"price.finalPrice\",\n"
        + "  \"facilityIds\": \"facilities.$.id\"\n"
        + "}\n";

    String outputString = "[\n"
        + "  {\n"
        + "    \"price\": 62000,\n"
        + "    \"inventoryId\": \"1\",\n"
        + "    \"facilityIds\": [\"ac\"]\n"
        + "  },\n"
        + "  {\n"
        + "    \"price\": 80000,\n"
        + "    \"inventoryId\": \"2\",\n"
        + "    \"facilityIds\": [\"ac\", \"vip-lounge\"]\n"
        + "  }\n"
        + "]\n";

    List<Map<String, Object>> list = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {});
    Map<String, String> mappingSpecs = mapper.readValue(mappingSpecsString, new TypeReference<Map<String, String>>() {});

    // Call the method under test
    List<Map<String, Object>> output = transformData(list, mappingSpecs);
    List<Map<String, Object>> expectedOutput = mapper.readValue(outputString, new TypeReference<List<Map<String, Object>>>() {});
    Assertions.assertEquals(expectedOutput, output);
  }

  @Test
  void levelTwo() throws JsonProcessingException {
    String jsonString = "{\n"
        + "  \"inventories\": [\n"
        + "    {\n"
        + "      \"id\": \"1\",\n"
        + "      \"name\": \"Regular\",\n"
        + "      \"price\": {\n"
        + "          \"finalPrice\": 62000\n"
        + "      },\n"
        + "      \"facilities\": [\n"
        + "        {\n"
        + "          \"name\": \"Full AC\",\n"
        + "          \"id\": \"ac\"\n"
        + "        }\n"
        + "      ]\n"
        + "    },\n"
        + "    {\n"
        + "      \"id\": \"2\",\n"
        + "      \"name\": \"Express\",\n"
        + "      \"price\": {\n"
        + "          \"finalPrice\": 80000\n"
        + "      },\n"
        + "      \"facilities\": [\n"
        + "        {\n"
        + "          \"name\": \"Full AC\",\n"
        + "          \"id\": \"ac\"\n"
        + "        },\n"
        + "        {\n"
        + "          \"name\": \"VIP Lounge\",\n"
        + "          \"id\": \"vip-lounge\"\n"
        + "        }\n"
        + "      ]\n"
        + "    }\n"
        + "  ],\n"
        + "  \"currency\": \"IDR\"\n"
        + "}\n";

    String mappingSpecsString = "{\n"
        + "  \"inventoryId\": \"inventories.$.id\",\n"
        + "  \"price\": \"inventories.$.price.finalPrice\",\n"
        + "  \"facilityIds\": \"inventories.$.facilities.$.id\",\n"
        + "  \"currency\": \"currency\"\n"
        + "}\n";

    String outputString = "[\n"
        + "  {\n"
        + "    \"price\": 62000,\n"
        + "    \"inventoryId\": 1,\n"
        + "    \"currency\": \"IDR\",\n"
        + "    \"facilityIds\": [\"ac\"]\n"
        + "  },\n"
        + "  {\n"
        + "    \"price\": 80000,\n"
        + "    \"inventoryId\": 2,\n"
        + "    \"currency\": \"IDR\",\n"
        + "    \"facilityIds\": [\"ac\", \"vip-lounge\"]\n"
        + "  }\n"
        + "]\n";

    Map<String, Object> inputData =
        mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
    Map<String, String> mappingSpecs = mapper.readValue(mappingSpecsString, new TypeReference<Map<String, String>>() {});
    String nestedKey = "inventories";

    // Call the method under test
    List<Map<String, Object>> output = transformDataWithNestedKey(inputData, mappingSpecs, nestedKey);
    List<Map<String, Object>> expectedOutput = mapper.readValue(outputString, new TypeReference<List<Map<String, Object>>>() {});
    Assertions.assertEquals(expectedOutput, output);
  }

  @Test
  void levelThree() throws JsonProcessingException {
    String jsonString = "{\n"
        + "  \"inventories\": [\n"
        + "    {\n"
        + "      \"id\": \"1\",\n"
        + "      \"name\": \"Regular\",\n"
        + "      \"price\": {\n"
        + "          \"finalPrice\": 62000\n"
        + "      },\n"
        + "      \"facilities\": [\n"
        + "        {\n"
        + "          \"name\": \"Full AC\",\n"
        + "          \"id\": \"ac\"\n"
        + "        }\n"
        + "      ]\n"
        + "    },\n"
        + "    {\n"
        + "      \"id\": \"2\",\n"
        + "      \"name\": \"Express\",\n"
        + "      \"price\": {\n"
        + "          \"finalPrice\": 80000\n"
        + "      },\n"
        + "      \"facilities\": [\n"
        + "        {\n"
        + "          \"name\": \"Full AC\",\n"
        + "          \"id\": \"ac\"\n"
        + "        },\n"
        + "        {\n"
        + "          \"name\": \"VIP Lounge\",\n"
        + "          \"id\": \"vip-lounge\"\n"
        + "        }\n"
        + "      ]\n"
        + "    }\n"
        + "  ],\n"
        + "  \"currency\": \"IDR\"\n"
        + "}\n";

    String mappingSpecsString = "{\n"
        + "  \"inventoryId\": \"inventories.$.id\",\n"
        + "  \"price\": \"inventories.$.price.finalPrice\",\n"
        + "  \"facilityIds\": \"inventories.$.facilities.$.id\",\n"
        + "  \"currency\": \"currency\",\n"
        + "  \"priceText\": \"currency\\\" \\\"inventories.$.price.finalPrice\"\n"
        + "}\n";

    String outputString = "[\n"
        + "  {\n"
        + "    \"price\": 62000,\n"
        + "    \"inventoryId\": 1,\n"
        + "    \"currency\": \"IDR\",\n"
        + "    \"facilityIds\": [\"ac\"],\n"
        + "    \"priceText\": \"IDR 62000\"\n"
        + "  },\n"
        + "  {\n"
        + "    \"price\": 80000,\n"
        + "    \"inventoryId\": 2,\n"
        + "    \"currency\": \"IDR\",\n"
        + "    \"facilityIds\": [\"ac\", \"vip-lounge\"],\n"
        + "    \"priceText\": \"IDR 80000\"\n"
        + "  }\n"
        + "]\n";

    Map<String, Object> inputData =
        mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
    Map<String, String> mappingSpecs = mapper.readValue(mappingSpecsString, new TypeReference<Map<String, String>>() {});
    String nestedKey = "inventories";

    // Call the method under test
    List<Map<String, Object>> output = transformDataWithNestedKey(inputData, mappingSpecs, nestedKey);
    List<Map<String, Object>> expectedOutput = mapper.readValue(outputString, new TypeReference<List<Map<String, Object>>>() {});
    Assertions.assertEquals(expectedOutput, output);
  }

  @Test
  void levelFour() throws JsonProcessingException {
    String jsonString = "{\n"
        + "  \"inventories\": [\n"
        + "    {\n"
        + "      \"id\": \"1\",\n"
        + "      \"name\": \"Regular\",\n"
        + "      \"price\": {\n"
        + "          \"finalPrice\": 62000\n"
        + "      },\n"
        + "      \"facilities\": [\n"
        + "        {\n"
        + "          \"name\": \"Full AC\",\n"
        + "          \"id\": \"ac\"\n"
        + "        }\n"
        + "      ],\n"
        + "      \"availableQuota\": 10\n"
        + "    },\n"
        + "    {\n"
        + "      \"id\": \"2\",\n"
        + "      \"name\": \"Express\",\n"
        + "      \"price\": {\n"
        + "          \"finalPrice\": 80000\n"
        + "      },\n"
        + "      \"facilities\": [\n"
        + "        {\n"
        + "          \"name\": \"Full AC\",\n"
        + "          \"id\": \"ac\"\n"
        + "        },\n"
        + "        {\n"
        + "          \"name\": \"VIP Lounge\",\n"
        + "          \"id\": \"vip-lounge\"\n"
        + "        }\n"
        + "      ],\n"
        + "      \"availableQuota\": 0\n"
        + "    }\n"
        + "  ],\n"
        + "  \"currency\": \"IDR\"\n"
        + "}\n";

    String outputString = "[\n"
        + "  {\n"
        + "    \"tripDurationText\": \"2 hours\",\n"
        + "    \"price\": 62000,\n"
        + "    \"inventoryId\": \"1\",\n"
        + "    \"currency\": \"IDR\",\n"
        + "    \"facilityIds\": [\"ac\"],\n"
        + "    \"priceText\": \"IDR 62000\"\n"
        + "  },\n"
        + "  {\n"
        + "    \"tripDurationText\": \"30 minutes\",\n"
        + "    \"price\": 80000,\n"
        + "    \"inventoryId\": \"2\",\n"
        + "    \"currency\": \"IDR\",\n"
        + "    \"facilityIds\": [\"ac\", \"vip-lounge\"],\n"
        + "    \"priceText\": \"Sold out\"\n"
        + "  }\n"
        + "]\n";

    Map<String, Object> inputData =
        mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});

    Map<String, String> mappingSpecs = new HashMap<>();
    mappingSpecs.put("inventoryId", "inventories.$.id");
    mappingSpecs.put("price", "inventories.$.price.finalPrice");
    mappingSpecs.put("facilityIds", "inventories.$.facilities.$.id");
    mappingSpecs.put("currency", "currency");
//    mappingSpecs.put("priceText", "\"awal \""
//        + "__IF__[inventories.$.availableQuota]__EQ__[\"0\"]"
//        + "__THEN__[\"Sold out\"]"
//        + "__ELSE__[currency\" \"inventories.$.price.finalPrice]__END__\""
//        + "\" tengahan \""
//        + "__IF__[inventories.$.availableQuota]__EQ__[\"0\"]"
//        + "__THEN__[\"Sold in\"]"
//        + "__ELSE__[currency\" \"inventories.$.price.finalPrice]__END__\""
//        + "\" akhir\"");

    mappingSpecs.put("priceText", "__IF__[inventories.$.availableQuota]__EQ__[\"0\"]"
        + "__THEN__[\"Sold out\"]__ELSE__[currency\" \"inventories.$.price.finalPrice]__END__");

    mappingSpecs.put("tripDurationText", "__IF__[inventories.$.name]"
        + "__CONTAINS__[\"Express\"]__THEN__[\"30 minutes\"]"
        + "__ELSE__[\"2 hours\"]__END__");
    String nestedKey = "inventories";

    // Call the method under test
    List<Map<String, Object>> output = transformDataWithNestedKey(inputData, mappingSpecs, nestedKey);
    List<Map<String, Object>> expectedOutput = mapper.readValue(outputString, new TypeReference<List<Map<String, Object>>>() {});
    Assertions.assertEquals(expectedOutput.toString(), output.toString());
  }


}
