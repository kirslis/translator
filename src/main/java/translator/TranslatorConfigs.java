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
    public static String yandexKey = "t1.9euelZrPj5uNjJ6Ymp7LiZuZmYuUnO3rnpWaisyayY2SzpTMio7HjpLPmMzl8_cgXwtK-e9sJlcS_t3z92ANCUr572wmVxL-zef1656VmsmZksuKzsqXisrOjpDNnJue7_zF656VmsmZksuKzsqXisrOjpDNnJue.jxpLVgLLjiqqE5h6asK8xzu4BXSpIkoieFKcxILse9JmjFhT9cZixguEu1te89Y44IacmXfR9qXkGkyJ1ZHGCA";
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