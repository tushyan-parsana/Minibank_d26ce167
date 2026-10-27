package edu.charusat.minibank.repository;
import edu.charusat.minibank.model.annotation.Id;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Repository<T> {
    private final Map<String,T> items = new ConcurrentHashMap<>();
    public T save(T entity) {
        Objects.requireNonNull(entity, "entity");
        String id = extractId(entity);
        items.put(id, entity);
        return entity;
    }
    @SafeVarargs public final void saveAll(T... entities) { for (T entity : entities) save(entity); }
    public T findById(String id) { return items.get(id); }
    public ArrayList<T> findAll() { return new ArrayList<>(items.values()); }
    public boolean delete(String id) { return items.remove(id) != null; }
    private String extractId(T entity) {
        Class<?> type = entity.getClass();
        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try { field.setAccessible(true); Object value = field.get(entity);
                        if (value == null) throw new IllegalArgumentException("@Id field cannot be null");
                        return value.toString();
                    } catch (ReflectiveOperationException e) { throw new IllegalArgumentException("Cannot read @Id field", e); }
                }
            }
            type = type.getSuperclass();
        }
        throw new IllegalArgumentException("Entity has no field annotated with @Id: " + entity.getClass().getName());
    }
}
