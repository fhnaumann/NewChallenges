package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class NullHelper {

    @NotNull
    public static JsonNode at(@NotNull JsonNode schemaRoot, @NotNull String path) {
        JsonNode jsonNodeAt = schemaRoot.at(path);
        if(jsonNodeAt.isMissingNode()) {
            throw new RuntimeException("Missing node at '%s'.".formatted(path));
        }
        return jsonNodeAt;
    }

    @NotNull
    public static JsonNode at(@NotNull JsonNode schemaRoot, @NotNull String definitionName, @NotNull String property) {
        return at(schemaRoot, "/definitions/%s/properties/%s".formatted(definitionName, property));
    }

    @NotNull
    public static int minValue(@NotNull JsonNode schemaRoot, @NotNull String definitionName, @NotNull String property) {
        JsonNode jsonNodeAt = at(schemaRoot, definitionName, property);
        return at(jsonNodeAt, "/minimum").asInt();
    }

    @NotNull
    public static int maxValue(@NotNull JsonNode schemaRoot, @NotNull String definitionName, @NotNull String property) {
        JsonNode jsonNodeAt = at(schemaRoot, definitionName, property);
        return at(jsonNodeAt, "/maximum").asInt();
    }

    @NotNull
    public static <T> T notNullOrDefault(@Nullable T objectInConfig, Class<T> clazz) {
        if(objectInConfig != null) {
            return objectInConfig;
        }
        try {
            T newObjectInConfig = clazz.getConstructor().newInstance();

            // TODO: the code below is untested. No idea if it works for nested complex objects...
            for(Field field : clazz.getDeclaredFields()) {
                if(field.getType().isPrimitive()) {
                    continue;
                }
                if(field.getType().equals(List.class)) {

                } else {
                    field.trySetAccessible();
                    T fieldObject = (T) field.get(newObjectInConfig);
                    field.set(newObjectInConfig, notNullOrDefault(fieldObject, (Class<T>) fieldObject.getClass()));
                }
            }
            return newObjectInConfig;
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
