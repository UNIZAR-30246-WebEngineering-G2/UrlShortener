package es.unizar.es.foodnet.model.service;


/**
 * A message to be displayed in web context. Depending on the type, different style will be applied.
 * Source: https://github.com/kolorobot/spring-mvc-quickstart-archetype/blob/master/src/main/resources/archetype-resources/src/main/java/support/web/Message.java
 */
public class Message implements java.io.Serializable {
    /**
     * Name of the flash attribute.
     */
    public static final String MESSAGE_ATTRIBUTE = "message";

    /**
     * The type of the message to be displayed. The type is used to show message in a different style.
     */
    public static enum Type {
        DANGER, WARNING, INFO, SUCCESS;
    }

    private final String message;
    private final Type type;
    private final String args;

    public Message(String message, Type type) {
        this.message = message;
        this.type = type;
        this.args = null;
    }

    public Message(String message, Type type, String args) {
        this.message = message;
        this.type = type;
        this.args = args;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public String getArgs() {
        return args;
    }
}