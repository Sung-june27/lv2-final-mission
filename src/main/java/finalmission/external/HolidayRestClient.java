package finalmission.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class HolidayRestClient {

    private final RestClient restClient;
    private final String baseUrl;
    private final String serviceKey;

    public List<Integer> getHolidayByMonth(LocalDate date) {
        String uriString = baseUrl + String.format(
                "?_type=json&solYear=%d&solMonth=%02d&ServiceKey=%s",
                date.getYear(), date.getMonthValue(), serviceKey);

        String response = restClient.get()
                .uri(uriString)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        return mapping(response);
    }

    private List<Integer> mapping(String json) {
        JsonNode items = mappingToJsonNode(json);

        // 데이터가 없는 경우 String 타입
        if(items.isTextual()) {
            return List.of();
        }

        JsonNode item = items.get("item");

        // 데이터가 1개인 경우 response가 JsonObject타입
        if (item.isObject()) {
            String locDate = item.get("locdate").asText();
            return List.of(Integer.parseInt(locDate.substring(6,8)));
        }

        // 데이터가 여러개인 경우 response가 JsonArray타입
        if (item.isArray()) {
            List<String> holidays = new ArrayList<>();
            Iterator<JsonNode> items2 = item.elements();
            while(items2.hasNext()) {
                holidays.add(items2.next().get("locdate").asText());
            }
            return holidays.stream()
                    .map(holiday -> Integer.parseInt(holiday.substring(6, 8)))
                    .toList();
        }

        // 데이터가 없는 경우 String 타입
        return null;
    }

    private static JsonNode mappingToJsonNode(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.get("response").get("body").get("items");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
