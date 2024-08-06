package translator.models;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static translator.TranslatorConfigs.*;


public class YandexTranslator {
    private String detectedLanguage = null;

    private String getTranslatedText(String response) {
        int startId = response.indexOf("text") + ("test").length() + 1;
        while (response.charAt(startId) != '"') startId++;

        startId++;

        StringBuilder text = new StringBuilder();
        while (response.charAt(startId) != '"') text.append(response.charAt(startId++));
        return text.toString();
    }

    private String getDetectedLanguage(String response) {
        int startId = response.indexOf("detectedLanguageCode") + ("detectedLanguageCode").length() + 1;
        while (response.charAt(startId) != '"') startId++;

        startId++;

        StringBuilder languageCode = new StringBuilder();
        while (response.charAt(startId) != '"') languageCode.append(response.charAt(startId++));
        return languageCode.toString();
    }

    private class translatePartTask implements Runnable {
        private final String url;
        private final HttpEntity<String> requestEntity;
        private final String[] words;
        private final int wordIndex;

        translatePartTask(String url, HttpEntity<String> requestEntity, String[] words, int wordIndex) {
            this.url = url;
            this.requestEntity = requestEntity;
            this.words = words;
            this.wordIndex = wordIndex;
        }

        @Override
        public void run() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            String response = restTemplate.postForEntity(url, requestEntity, String.class).getBody();

            words[wordIndex] = getTranslatedText(response);
            if (detectedLanguage == null)
                detectedLanguage = getDetectedLanguage(response);
        }
    }

    private Map<String, String> getParams(String originalLanguage, String languageToTranslate) {
        Map<String, String> params = new HashMap<>();
        params.put("encoding", "UTF-16");
        params.put("forceEncoding", "true");
        params.put("folderId", folderId);
        params.put("targetLanguageCode", languageToTranslate);
        if (!Objects.equals(originalLanguage, ""))
            params.put("sourceLanguageCode", originalLanguage);

        return params;
    }

    private Pair<String, String> translate(String originalText, String originalLanguage, String languageToTranslate) {
        String[] words = originalText.split(" ");
        String url = yandexURL + "translate";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + yandexKey);

        Map<String, String> params = getParams(originalLanguage, languageToTranslate);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        for (int i = 0; i < words.length; i++) {
            params.put("texts", words[i]);
            HttpEntity<String> requestEntity;
            try {
                String reqBodyData = new ObjectMapper().writeValueAsString(params);
                requestEntity = new HttpEntity<>(reqBodyData, headers);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            executor.execute(new translatePartTask(url, requestEntity, words, i));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {
        }

        return new Pair<>(StringUtils.join(words, ' '), detectedLanguage);
    }

    public Pair<String, String> translateWithLanguageAutoDetection(String originalText, String languageToTranslate) {
        return translate(originalText, "", languageToTranslate);
    }

    public String translateWithSourceLanguage(String originalText, String originalLanguage, String languageToTranslate) {
        return translate(originalText, originalLanguage, languageToTranslate).getKey();
    }

    public boolean checkLanguageCode(String language) {
        for (int i = 0; i < availableLanguages.size(); i++)
            if (Objects.equals(language, availableLanguages.get(i))) return true;

        return false;
    }
}
