package be.joengenduvel.java.verifiers;

import org.junit.Test;

public class ToStringVerifierTest {

    @Test
    public void shouldPassWhenToStringIsImplementedCorrectly() {
        ToStringVerifier.forClass(ClassWithToString.class).containsAllPrivateFields(new ClassWithToString());
    }

    @Test(expected = WrongToStringImplementationException.class)
    public void shouldFailWhenToStringIsNotImplemented() {
        ToStringVerifier.forClass(ClassWithoutToString.class).containsAllPrivateFields(new ClassWithoutToString());
    }

    @Test(expected = WrongToStringImplementationException.class)
    public void shouldFailWhenProvidedWithNull() {
        ToStringVerifier.forClass(ClassWithToString.class).containsAllPrivateFields(null);
    }

    @Test
    public void shouldIgnoreJacocoFields() {
        ToStringVerifier.forClass(ClassContainingJacocoData.class).containsAllPrivateFields(new ClassContainingJacocoData());
    }

    @Test(expected = WrongToStringImplementationException.class)
    public void shouldFailWhenFieldIsNotPresentInToString() {
        ToStringVerifier.forClass(ClassWithWrongToString.class).containsAllPrivateFields(new ClassWithWrongToString());
    }

    @Test
    public void shouldBeAbleToIgnoreFields() {
        ToStringVerifier.forClass(ClassWithWrongToString.class).ignore("field2").containsAllPrivateFields(new ClassWithWrongToString());
    }

    private class ClassWithoutToString {
        private final int field2 = 2;
        private String field1 = "field1";
    }

    private class ClassWithToString {
        private final int field2 = 2;
        private String field1 = "field1";
        private Object field3 = null;

        @Override
        public String toString() {
            return "ClassWithToString{" +
                    "field2=" + field2 +
                    ", field1='" + field1 + '\'' +
                    ", field3=" + field3 +
                    '}';
        }

    }

    private class ClassWithWrongToString {
        private final int field2 = 2;
        private String field1 = "field1";

        @Override
        public String toString() {
            return "ClassWithToString{" +
                    "field1='" + field1 + '}';
        }
    }

    private class ClassContainingJacocoData {
        private final int field2 = 2;
        private Object $jacocoData = "Some data";
        private String field1 = "field1";

        @Override
        public String toString() {
            return "ClassWithToString{" +
                    "field1='" + field1 + '\'' +
                    ", field2=" + field2 +
                    '}';
        }
    }
}