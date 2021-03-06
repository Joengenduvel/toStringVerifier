package be.joengenduvel.java.verifiers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToStringVerifier<T> {

    private static final List<String> FIELDS_TO_ALWAYS_IGNORE = ConfigurationManager.getInstance().getFieldsToIgnore();
    private final Class<? extends T> classToVerify;
    private final List<String> fieldsToIgnore;


    private ToStringVerifier(Class<? extends T> classToVerify) {
        this.classToVerify = classToVerify;
        this.fieldsToIgnore = new ArrayList<>();
    }

    public static <R> ToStringVerifier<R> forClass(Class<? extends R> classToVerify) {
        return new ToStringVerifier<>(classToVerify);
    }

    public ToStringVerifier<T> ignore(String... fieldNames) {
        fieldsToIgnore.addAll(Arrays.asList(fieldNames));
        return this;
    }

    public void containsAllPrivateFields(T objectToTest) {
        String toString = getToString(objectToTest);
            for (Field privateDeclaredField : getDeclaredPrivateFields(classToVerify)) {
                if (!toString.contains(privateDeclaredField.getName())) {
                    throw WrongToStringImplementationException.forFieldNameNotFound(privateDeclaredField, classToVerify, toString);
                }
                String fieldValueString;
                try {
                    fieldValueString = String.valueOf(privateDeclaredField.get(objectToTest));
                    if (!toString.contains(fieldValueString)) {
                        throw WrongToStringImplementationException.forValueNotFound(privateDeclaredField, fieldValueString, classToVerify, toString);
                    }
                } catch (IllegalAccessException e) {
                    throw WrongToStringImplementationException.forIllegalAccess(privateDeclaredField, classToVerify, e);
                }
            }

    }


    public void containsClassName(T objectToTest) {
        String toString = getToString(objectToTest);
        if (!toString.contains(classToVerify.getSimpleName())) {
            throw WrongToStringImplementationException.forClassNameNotFound(toString, classToVerify.getSimpleName(), classToVerify);
        }
    }

    public void verify(T objectToTest) {
        containsClassName(objectToTest);
        containsAllPrivateFields(objectToTest);
    }

    private String getToString(T objectToTest) {
        verifyNotNull(objectToTest);
        return objectToTest.toString();
    }

    private void verifyNotNull(T objectToTest) {
        if (objectToTest == null) {
            throw WrongToStringImplementationException.forObjectNull(classToVerify);
        }
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
}