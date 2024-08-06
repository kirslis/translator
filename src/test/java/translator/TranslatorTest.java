package translator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import translator.models.Translation;

import java.util.Objects;

import static translator.TranslatorConfigs.updateLanguages;

@SpringBootTest
public class TranslatorTest {

    @BeforeAll
    static void setup() {
        updateLanguages();
    }

    @Test
    void whenCreateTranslationWithoutText_thenWithoutTranslate() {
        Translation t = new Translation("", "", "");
        assert (Objects.equals(t.getTranslatedText(), ""));
    }

    @Test
    void whenCreateTranslationWithoutOriginalLanguage_thenLanguageAutomaticallyDetected() {
        Translation t = new Translation("текст на русском", "", "en");
        assert (Objects.equals(t.getOriginalLanguage(), "ru"));
    }

    @Test
    void whenCreateTranslationWithWrongOriginalLanguage_thenOriginalLanguageWrongCodeIsTrue() {
        Translation t = new Translation("текст на русском", "arr", "en");
        assert (t.isOriginalLanguageWrongCode());
    }

    @Test
    void whenCreateTranslationWithWrongLanguageToTranslate_thenLanguageToTranslateWrongCodeIsTrue() {
        Translation t = new Translation("текст на русском", "ru", "arr");
        assert (t.isLanguageToTranslateWrongCode());
    }

    @Test
    void simpleTranslationFromRussianToEnglish() {
        Translation t = new Translation("текст на русском", "ru", "en");
        assert (Objects.equals(t.getTranslatedText(), "text on Russian"));
    }

    @Test
    void simpleTranslationToRussian(){
        Translation t = new Translation("the text is in Russian", "en", "ru");
        assert (Objects.equals(t.getTranslatedText(), "то текст является в Русский"));
    }

    @Test
    void SimpleTranslationWithAutoDetectedLanguage_ResultCheck(){
        Translation t = new Translation("the text is in Russian", "", "ru");
        assert (Objects.equals(t.getTranslatedText(), "то текст является в Русский"));
    }


    @Test
    void SimpleTranslationWithAutoDetectedLanguage_OriginalLanguageCheck(){
        Translation t = new Translation("the text is in Russian", "", "ru");
        assert (Objects.equals(t.getOriginalLanguage(), "en"));
    }
}
