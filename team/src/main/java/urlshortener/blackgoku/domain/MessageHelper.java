package es.unizar.es.foodnet.model.service;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static es.unizar.es.foodnet.model.service.Message.MESSAGE_ATTRIBUTE;

/**
 * Esta clase sirve para los distintos tipos de alertas que existen
 * Fuente: https://github.com/kolorobot/spring-mvc-quickstart-archetype/blob/master/src/main/resources/archetype-resources/src/main/java/support/web/MessageHelper.java
 */
public final class MessageHelper {

    private MessageHelper() {

    }

    /**
     * Alerta de exito con redirectAttributes
     */
    public static void addSuccessAttribute(RedirectAttributes ra, String message, String args) {
        addAttribute(ra, message, Message.Type.SUCCESS, args);
    }

    /**
     * Alerta de error con redirectAttributes
     */
    public static void addErrorAttribute(RedirectAttributes ra, String message, String args) {
        addAttribute(ra, message, Message.Type.DANGER, args);
    }

    /**
     * Alerta de info con redirectAttributes
     */
    public static void addInfoAttribute(RedirectAttributes ra, String message, String args) {
        addAttribute(ra, message, Message.Type.INFO, args);
    }

    /**
     * Alerta de warning con redirectAttributes
     */
    public static void addWarningAttribute(RedirectAttributes ra, String message, String args) {
        addAttribute(ra, message, Message.Type.WARNING, args);
    }

    /**
     * Metodo que enlaza las alertas
     */
    private static void addAttribute(RedirectAttributes ra, String message, Message.Type type, String args) {
        ra.addFlashAttribute(MESSAGE_ATTRIBUTE, new Message(message, type, args));
    }

    /**
     * Alerta de exito con model
     */
    public static void addSuccessAttribute(Model model, String message, String args) {
        addAttribute(model, message, Message.Type.SUCCESS, args);
    }

    /**
     * Alerta de error con model
     */
    public static void addErrorAttribute(Model model, String message, String args) {
        addAttribute(model, message, Message.Type.DANGER, args);
    }

    /**
     * Alerta de info con model
     */
    public static void addInfoAttribute(Model model, String message, String args) {
        addAttribute(model, message, Message.Type.INFO, args);
    }

    /**
     * Alerta de warning con model
     */
    public static void addWarningAttribute(Model model, String message, String args) {
        addAttribute(model, message, Message.Type.WARNING, args);
    }

    /**
     * Enlace de alertas con model
     */
    private static void addAttribute(Model model, String message, Message.Type type, String args) {
        model.addAttribute(MESSAGE_ATTRIBUTE, new Message(message, type, args));
    }
}