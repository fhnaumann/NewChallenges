package wand555.github.io.challenges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.mapping.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public record DataSourceContext(List<MaterialJSON> materialJSONList, List<EntityTypeJSON> entityTypeJSONList) {

    public <T extends DataSourceJSON<K>, K extends Keyed> List<T> getTypedList(T dataSourceElement) {
        MaterialJSON materialJSON = materialJSONList.get(0);
        EntityTypeJSON entityTypeJSON = entityTypeJSONList.get(0);
        if(dataSourceElement.getClass() == materialJSON.getClass()) {
            return (List<T>) materialJSONList;
        }
        if(dataSourceElement.getClass() == entityTypeJSON.getClass()) {
            return (List<T>) entityTypeJSONList;
        }
        else {
            throw new RuntimeException("'%s' is not a valid class. Valid classes are %s".formatted(dataSourceElement, List.of(materialJSON.getClass(), entityTypeJSON.getClass())));
        }
    }

    public static final class Builder {
        private List<MaterialJSON> materialJSONList;
        private List<EntityTypeJSON> entityTypeJSONList;

        public Builder withMaterialJSONList(InputStream materialJSONInputStream) {
            try {
                this.materialJSONList = new ObjectMapper().readValue(materialJSONInputStream, MaterialDataSource.class).getData();
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Builder withEntityTypeJSONList(InputStream entityTypeJSONInputStream) {
            try {
                this.entityTypeJSONList = new ObjectMapper().readValue(entityTypeJSONInputStream, EntityTypeDataSource.class).getData();
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public DataSourceContext build() {
            return new DataSourceContext(materialJSONList, entityTypeJSONList);
        }
    }

}
