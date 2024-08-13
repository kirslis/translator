package translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class TranslatorConfigs {
    public static String yandexURL = "https://translate.api.cloud.yandex.net/translate/v2/";
    public static List<String> availableLanguages = new ArrayList<String>();
    public static String yandexKey = "t1.9euelZqMyJiZjJSSyorOkJeJl47Nyu3rnpWaisyayY2SzpTMio7HjpLPmMzl9PcUPRNK-e9HaX-x3fT3VGsQSvnvR2l_sc3n9euelZqKmY6diZrPlMrOzJuej42Nku_8xeuelZqKmY6diZrPlMrOzJuej42Nkg.M5z6nfMTctQL5k-1Sc5tFfs9q1SZ5szeWPvaJU-WnS4J6KfYZR3gzd4oW5FAW5feiRZ7uFhS3-wdVrxQB18dAA";
    public static String folderId = "b1gh6pi76qpk5nbokd7o";

    private static List<String> getAllLanguagesFromText(String text) {
        List<String> languages = new ArrayList<>();
        int lastIndex = text.indexOf("\"code\"");
        while (lastIndex != -1) {
            lastIndex += "\"code\"".length() + 1;
            int startCodeIndex = text.indexOf("\"", lastIndex);
            int endCodeIndex = text.indexOf("\"", startCodeIndex + 1);
            languages.add(text.substring(startCodeIndex + 1, endCodeIndex));

            lastIndex = text.indexOf("\"code\"", lastIndex);
        }

        return languages;
    }

    public static void updateLanguages() {
        RestTemplate restTemplate = new RestTemplate();

        String url = yandexURL + "/languages";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + yandexKey);

        Map<String, String> params = new HashMap<String, String>();
        params.put("folderId", "b1gh6pi76qpk5nbokd7o");

        HttpEntity<String> requestEntity;
        try {
            String reqBodyData = new ObjectMapper().writeValueAsString(params);
            requestEntity = new HttpEntity<>(reqBodyData, headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ResponseEntity<String> responseEntity = restTemplate
                .postForEntity(url, requestEntity, String.class);

        availableLanguages = getAllLanguagesFromText(responseEntity.getBody());
    }
}