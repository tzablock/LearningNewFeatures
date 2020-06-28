package com.rxjavaimpl.annotations;

public class TestStart {
    public static void main(String[] args) {
        Person person = new Person();
        person.setAge("32");
        person.setAddress("sjssjsj");
        person.setFirstName("Marek");
        person.setLastName("Grocki");

        AnnotationProcessor processor = new AnnotationProcessor();
        if (processor.checkIfSerializable(person)){
            processor.invoke(person);
            String json = processor.createJson(person);
            System.out.println(json);
        }
    }
}
