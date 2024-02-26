package wand555.github.io.challenges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import wand555.github.io.challenges.mapping.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public record DataSourceContext(List<MaterialJSON> materialJSONList, List<EntityTypeJSON> entityTypeJSONList) {

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
