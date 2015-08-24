package be.joengenduvel.java.verifiers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToStringVerifier<T> {

    private static final List<String> FIELDS_TO_ALWAYS_IGNORE = Arrays.asList("$jacocoData");
    private final Class<T> classToVerify;
    private final List<String> fieldsToIgnore;


    private ToStringVerifier(Class<T> classToVerify) {
        this.classToVerify = classToVerify;
        this.fieldsToIgnore = new ArrayList<>();
    }

    public static <R> ToStringVerifier<R> forClass(Class<R> classToVerify) {
        return new ToStringVerifier<>(classToVerify);
    }

    public ToStringVerifier<T> ignore(String... fieldNames) {
        fieldsToIgnore.addAll(Arrays.asList(fieldNames));
        return this;
    }

    public void containsAllPrivateFields(T objectToTest) {
        try {
            String toString = objectToTest.toString();
            for (Field privateDeclaredField : getDeclaredPrivateFields(classToVerify)) {
                if (!toString.contains(privateDeclaredField.getName())) {
                    fieldNameNotFound(toString, privateDeclaredField);
                }
                String fieldValueString = null;
                try {
                    fieldValueString = String.valueOf(privateDeclaredField.get(objectToTest));
                    if (!toString.contains(fieldValueString)) {
                        valueNotFound(toString, privateDeclaredField, fieldValueString);
                    }
                } catch (IllegalAccessException e) {
                    fail(toString, privateDeclaredField, fieldValueString, e);
                }
            }
        } catch (NullPointerException e) {
            throw new WrongToStringImplementationException(classToVerify, e);
        }
    }

    private void fieldNameNotFound(String toString, Field privateDeclaredField) {
        fail(toString, privateDeclaredField, null, null);
    }


    private List<Field> getDeclaredPrivateFields(Class<?> aClass) {
        ArrayList<Field> privateDeclaredFields = new ArrayList<>();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (Modifier.isPrivate(declaredField.getModifiers()) && !fieldNeedsToBeIgnored(declaredField.getName())) {
                declaredField.setAccessible(true);
                privateDeclaredFields.add(declaredField);
            }
        }
        return privateDeclaredFields;
    }


    private boolean fieldNeedsToBeIgnored(String name) {
        return FIELDS_TO_ALWAYS_IGNORE.contains(name) || fieldsToIgnore.contains(name);
    }


    private void valueNotFound(String toString, Field privateDeclaredField, String fieldValueString) {
        fail(toString, privateDeclaredField, fieldValueString, null);
    }

    private void fail(String toString, Field field, String fieldValue, Throwable cause) {
        throw new WrongToStringImplementationException(toString, field, fieldValue, cause);
    }
}