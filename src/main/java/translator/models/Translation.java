package translator.models;

import javafx.util.Pair;

import java.util.Objects;

public class Translation {
    private final String originalText;
    private String translatedText = "";
    private String originalLanguage ;
    private final String languageToTranslate;
    private boolean originalLanguageWrongCode = false;
    private boolean languageToTranslateWrongCode = false;

    public boolean isOriginalLanguageWrongCode() {
        return originalLanguageWrongCode;
    }

    public boolean isLanguageToTranslateWrongCode() {
        return languageToTranslateWrongCode;
    }

    public String getLanguageToTranslate() {
        return languageToTranslate;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public Translation(String originalText, String originalLanguage, String languageToTranslate) {
        this.originalText = originalText;
        YandexTranslator yt = new YandexTranslator();
        if (Objects.equals(originalLanguage, ""))
            originalLanguageWrongCode = false;
        else
            originalLanguageWrongCode = !yt.checkLanguageCode(originalLanguage);
        this.originalLanguage = originalLanguage;

        languageToTranslateWrongCode = !yt.checkLanguageCode(languageToTranslate);
        if (!(originalLanguageWrongCode || languageToTranslateWrongCode) && !Objects.equals(originalText, "")) {
            if (Objects.equals(originalLanguage, "")) {
                Pair<String, String> textAndLanguage = yt.translateWithLanguageAutoDetection(originalText, languageToTranslate);
                this.translatedText = textAndLanguage.getKey();
                this.originalLanguage = textAndLanguage.getValue();
            } else {
                this.translatedText = yt.translateWithSourceLanguage(originalText, originalLanguage, languageToTranslate);
            }
        }
        this.languageToTranslate = languageToTranslate;
    }

    public Translation() {
        this.originalText = "";
        this.translatedText = "";
        this.originalLanguage = "";
        this.languageToTranslate = "";
    }
}
