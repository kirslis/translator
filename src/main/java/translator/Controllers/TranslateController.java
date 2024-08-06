package translator.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import translator.DAO.TranslateRequestDao;
import translator.models.Translation;

@RestController
@RequestMapping("/translate")
public class TranslateController {
    @Autowired
    TranslateRequestDao tDao;

    @GetMapping()
    public ModelAndView openTranslation(Model model) {
        Translation translation = new Translation();
        model.addAttribute("translation", translation);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("translationPage.html");
        return modelAndView;
    }

    @PostMapping()
    public ModelAndView getTranslation(@RequestParam String textToTranslate, @RequestParam String originalLanguage, @RequestParam String languageToTranslate, Model model) {
        Translation translation = new Translation(textToTranslate, originalLanguage, languageToTranslate);

        if (translation.isLanguageToTranslateWrongCode())
            model.addAttribute("languageToTranslateErrorMassage", "wrong language to translate");
        else if (translation.isOriginalLanguageWrongCode())
            model.addAttribute("originalLanguageErrorMassage", "wrong original language");
        else {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            String clientIP = request.getRemoteAddr();
            tDao.addTranslation(translation, clientIP);
        }
        model.addAttribute("translation", translation);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("translationPage.html");
        return modelAndView;
    }
}
