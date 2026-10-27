package edu.charusat.minibank.util;
import edu.charusat.minibank.model.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
public final class AnnotationValidator {
    private AnnotationValidator() {}
    public static String[] validate(Object obj) {
        ArrayList<String> errors = new ArrayList<>();
        if (obj == null) return new String[]{"Object cannot be null"};
        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (field.isAnnotationPresent(Positive.class) && value instanceof Number n && n.doubleValue() < 0) {
                    errors.add(field.getName() + " " + field.getAnnotation(Positive.class).message());
                }
                if (field.isAnnotationPresent(MaxLength.class) && value instanceof String s && s.length() > field.getAnnotation(MaxLength.class).value()) {
                    errors.add(field.getName() + " exceeds max length " + field.getAnnotation(MaxLength.class).value());
                }
            } catch (ReflectiveOperationException | RuntimeException e) {
                errors.add("Could not validate field " + field.getName());
            }
        }
        return errors.toArray(new String[0]);
    }
}
