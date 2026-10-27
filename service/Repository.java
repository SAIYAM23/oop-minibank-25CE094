package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.annotation.Id;

public class Repository<T> {

    private final HashMap<String, T> entities = new HashMap<>();

    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null.");
        }

        String id = extractId(entity);
        entities.put(id, entity);

        return entity;
    }

    public T findById(String id) {
        return entities.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(entities.values());
    }

    public boolean delete(String id) {
        return entities.remove(id) != null;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    private String extractId(T entity) {
        Class<?> currentClass = entity.getClass();

        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);

                        if (value == null) {
                            throw new IllegalArgumentException(
                                    "Entity ID cannot be null.");
                        }

                        return value.toString();

                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(
                                "Unable to read entity ID.", e);
                    }
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        throw new IllegalArgumentException(
                "No field annotated with @Id found in "
                        + entity.getClass().getName());
    }

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(
                    "Values cannot be null or empty.");
        }

        double sum = 0;

        for (N value : values) {
            if (value == null) {
                throw new IllegalArgumentException(
                        "Values cannot contain null.");
            }

            sum += value.doubleValue();
        }

        return sum / values.length;
    }
}
