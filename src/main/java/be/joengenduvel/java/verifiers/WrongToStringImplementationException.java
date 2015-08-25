package be.joengenduvel.java.verifiers;

import java.lang.reflect.Field;

public class WrongToStringImplementationException extends RuntimeException {


    private static final String OBJECT_NULL_MESSAGE = "Cannot verify '%s' because the object provided was null.";
    private static final String FIELD_NOT_ACCESSABLE = "Could not verify field '%s' in class '%s' because %s";
    private static final String VALUE_NOT_FOUND = "Could not find the value '%s' of '%s' in '%s' for class '%s'";
    private static final String FIELD_NOT_FOUND = "Could not find field '%s' in '%s' for class '%s'";
    private static final String CLASS_NAME_NOT_FOUND = "Could not find the class name '%s' in %s for class %s";

    private WrongToStringImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongToStringImplementationException(String message) {
        this(message, null);
    }

    public static WrongToStringImplementationException forObjectNull(Class<?> classToVerify) {
        return new WrongToStringImplementationException(String.format(OBJECT_NULL_MESSAGE, classToVerify));
    }

    public static WrongToStringImplementationException forIllegalAccess(Field field, Class<?> classToVerify, IllegalAccessException cause) {
        return new WrongToStringImplementationException(String.format(FIELD_NOT_ACCESSABLE, field.getName(), classToVerify, cause.getMessage()), cause);
    }

    public static WrongToStringImplementationException forValueNotFound(Field field, String fieldValue, Class<?> classToVerify, String toString) {
        return new WrongToStringImplementationException(String.format(VALUE_NOT_FOUND, fieldValue, field.getName(), toString, classToVerify));
    }

    public static WrongToStringImplementationException forFieldNameNotFound(Field field, Class<?> classToVerify, String toString) {
        return new WrongToStringImplementationException(String.format(FIELD_NOT_FOUND, field.getName(), toString, classToVerify));
    }

    public static WrongToStringImplementationException forClassNameNotFound(String toString, String classToVerifyName, Class<?> classToVerify) {
        return new WrongToStringImplementationException(String.format(CLASS_NAME_NOT_FOUND, classToVerifyName, toString, classToVerify));
    }
}
