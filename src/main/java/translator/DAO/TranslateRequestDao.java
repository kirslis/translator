package translator.DAO;

import org.springframework.stereotype.Component;
import translator.models.Translation;

import java.sql.*;

@Component
public class TranslateRequestDao {
    public void addTranslation(Translation newTranslation, String ip){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/translationDB", "postgres", "bahamuth");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            PreparedStatement s = null;
            try {
                s = connection.prepareStatement("INSERT INTO \"translationRequest\" (request_ip, original_text, translated_text, original_language, translated_language) values(?, ?, ?, ?, ?)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                s.setString(1, ip);
                s.setString(2, newTranslation.getOriginalText());
                s.setString(3, newTranslation.getTranslatedText());
                s.setString(4, newTranslation.getOriginalLanguage());
                s.setString(5, newTranslation.getLanguageToTranslate());
                s.executeUpdate();

            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        }
    }
}
