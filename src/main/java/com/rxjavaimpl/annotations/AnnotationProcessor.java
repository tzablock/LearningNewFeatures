package com.rxjavaimpl.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotationProcessor {
    public boolean checkIfSerializable(Object object) {
        if (Objects.isNull(object)) {
            return false;
        }
        Class<?> clazz = object.getClass();
        return clazz.isAnnotationPresent(JsonSeriazable.class);
    }

    public void invoke(Object object) {
        Class<?> clazz = object.getClass();
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(
                        m -> m.isAnnotationPresent(Init.class)
                ).forEach(
                m -> invokeMethod(object, m)
        );
    }

    public String createJson(Object object) {
        Class<?> clazz = object.getClass();
        return "{" +
                Arrays.stream(clazz.getDeclaredFields())
                .filter(
                        f -> {
                            f.setAccessible(true);
                            return f.isAnnotationPresent(JsonElement.class);
                        }
                ).map(
                        f -> creteJsonElement(object, f)
                ).collect(Collectors.joining(",")) +
                "}";

    }


    private String creteJsonElement(Object object, Field f) {
        try {
            f.setAccessible(true);
            String key = f.getAnnotation(JsonElement.class).key();
            key = key.isEmpty() ? f.getName() : key;
            return String.format("\"%s\":\"%s\"", key, f.get(object));
        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    private void invokeMethod(Object object, Method m) {
        try {
            m.setAccessible(true);
            m.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
