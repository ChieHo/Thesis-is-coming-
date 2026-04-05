package de.propra.profil.web.ErrorPages;

import de.propra.profil.web.ErrorPages.CustomErrors.ProfileNotFoundException;
import de.propra.profil.application.exception.FileNotFoundException;
import de.propra.profil.application.exception.NichtVorhandenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ErrorPageController {


    /*
    // TODO: needed? Whitelabel Error Page is enough for unknown errors
    // generic error handler
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handle(HttpServletRequest request, Exception e) {
        System.out.println(e.getClass());
        ModelAndView mav = new ModelAndView();
        mav.addObject("path", request.getRequestURL());
        mav.addObject("cause", e.getCause());
        mav.addObject("class", e.getClass());
        mav.setViewName("errors/generic");

        return mav;
    }
    */

    // TODO: remove? login page is shown when logged out
    /*
    @ResponseStatus(HttpStatus.NOT_FOUND)
    // FIXME
    @ExceptionHandler()
    public String handle401(Model model, HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURL());
        return "errors/404";
    }
    */

    // deliberately return 404 where 403 would be used to conceal routes
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public String handle403(Model model, HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURL());
        return "errors/404";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handle404(Model model, HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURL());
        return "errors/404";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProfileNotFoundException.class)
    public String handleProfileNotFound(Model model, ProfileNotFoundException exception) {
        model.addAttribute("id", exception.getProfileId());
        return "errors/404profile";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NichtVorhandenException.class)
    public String handleNichtVorhanden(Model model) {
        return "errors/404profile";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFound(Model model) {
        return "errors/404";
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handle405(Model model, HttpServletRequest request) {
        model.addAttribute("method", request.getMethod());
        return "errors/405";
    }

    /*
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    // FIXME
    @ExceptionHandler()
    public String handle500() {
        return "errors/500";
    }
    */


}
