package be.joengenduvel.java.verifiers;

import java.lang.reflect.Field;

public class WrongToStringImplementationException extends RuntimeException {


    public WrongToStringImplementationException(String toString, Field privateDeclaredField, String fieldValue, Throwable throwable) {
        super(String.format("Failed to find field '%s' with value '%s' in '%s because %s", privateDeclaredField.getName(), fieldValue, toString, throwable.getMessage()), throwable);
    }

    public WrongToStringImplementationException(Class<?> classToVerify, Throwable throwable) {
        super(String.format("Something went wrong during the validation of %s", classToVerify.getName()), throwable);
    }
}
