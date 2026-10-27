package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[]{"Object cannot be null"};
        }

        List<String> errors = new ArrayList<>();

        // Check fields in the class and all its parent classes.
        Class<?> currentClass = obj.getClass();

        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive positive =
                                field.getAnnotation(Positive.class);

                        if (value instanceof Number number
                                && number.doubleValue() <= 0) {
                            errors.add(
                                    field.getName() + ": "
                                    + positive.message()
                            );
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength maxLength =
                                field.getAnnotation(MaxLength.class);

                        if (value instanceof String string
                                && string.length() > maxLength.value()) {
                            errors.add(
                                    field.getName()
                                    + ": maximum length is "
                                    + maxLength.value()
                            );
                        }
                    }

                    // @Id identifies a field; it needs no validation rule here.
                    if (field.isAnnotationPresent(Id.class)) {
                        // The identifier annotation is detected using reflection.
                    }

                } catch (IllegalAccessException
                        | RuntimeException e) {
                    errors.add(
                            "Unable to validate field: " + field.getName()
                    );
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}