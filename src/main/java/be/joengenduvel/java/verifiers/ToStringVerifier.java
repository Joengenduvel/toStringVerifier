package be.joengenduvel.java.verifiers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

public class ToStringVerifier<T> {

    private final Class<T> classToVerify;


    private ToStringVerifier(Class<T> classToVerify) {
        this.classToVerify = classToVerify;
    }

    public static <R> ToStringVerifier<R> forClass(Class<R> classToVerify) {
        return new ToStringVerifier<R>(classToVerify);
    }

    public void containsAllPrivateFields(T objectToTest) {
        try {
            String toString = objectToTest.toString();
            for (Field privateDeclaredField : getDeclaredPrivateFields(classToVerify)) {
                assertThat(toString, containsString(privateDeclaredField.getName()));
                String fieldValue = null;
                try {
                    fieldValue = privateDeclaredField.get(objectToTest).toString();
                    assertThat(toString, containsString(fieldValue));
                } catch (IllegalAccessException e) {
                    throw new WrongToStringImplementation(toString, privateDeclaredField, fieldValue, e);
                }
            }
        } catch (AssertionError e) {
            throw new WrongToStringImplementation(classToVerify, e);
        }
    }

    private List<Field> getDeclaredPrivateFields(Class<?> aClass) {
        ArrayList<Field> privateDeclaredFields = new ArrayList<Field>();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (Modifier.isPrivate(declaredField.getModifiers())) {
                declaredField.setAccessible(true);
                privateDeclaredFields.add(declaredField);
            }
        }
        return privateDeclaredFields;
    }
}