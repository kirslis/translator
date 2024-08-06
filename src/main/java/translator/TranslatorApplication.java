package translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

import static translator.TranslatorConfigs.updateLanguages;

@RestController
@SpringBootApplication
public class TranslatorApplication {

    public static void main(String[] args) {
        updateLanguages();

        SpringApplication.run(TranslatorApplication.class, args);
    }
}
