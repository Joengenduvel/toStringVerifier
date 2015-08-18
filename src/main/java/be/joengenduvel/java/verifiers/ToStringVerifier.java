package be.joengenduvel.java.verifiers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

public class ToStringVerifier<T> {

    private static final List<String> FIELDS_TO_IGNORE = Arrays.asList("$jacocoData");
    private final Class<T> classToVerify;


    private ToStringVerifier(Class<T> classToVerify) {
        this.classToVerify = classToVerify;
    }

    public static <R> ToStringVerifier<R> forClass(Class<R> classToVerify) {
        return new ToStringVerifier<>(classToVerify);
    }

    public void containsAllPrivateFields(T objectToTest) {
        try {
            String toString = objectToTest.toString();
            for (Field privateDeclaredField : getDeclaredPrivateFields(classToVerify)) {
                assertThat(toString, containsString(privateDeclaredField.getName()));
                String fieldValueString = null;
                try {
                    Object fieldValue = privateDeclaredField.get(objectToTest);
                    if (fieldValue == null) {
                        fieldValueString = "null";
                    } else {
                        fieldValueString = fieldValue.toString();
                    }
                    assertThat(toString, containsString(fieldValueString));
                } catch (IllegalAccessException e) {
                    throw new WrongToStringImplementation(toString, privateDeclaredField, fieldValueString, e);
                }
            }
        } catch (NullPointerException | AssertionError e) {
            throw new WrongToStringImplementation(classToVerify, e);
        }
    }

    private List<Field> getDeclaredPrivateFields(Class<?> aClass) {
        ArrayList<Field> privateDeclaredFields = new ArrayList<>();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (Modifier.isPrivate(declaredField.getModifiers()) && !FIELDS_TO_IGNORE.contains(declaredField.getName())) {
                declaredField.setAccessible(true);
                privateDeclaredFields.add(declaredField);
            }
        }
        return privateDeclaredFields;
    }
}