package wand555.github.io.challenges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import wand555.github.io.challenges.mapping.MaterialDataSource;
import wand555.github.io.challenges.mapping.MaterialJSON;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public record DataSourceContext(List<MaterialJSON> materialJSONList) {

    public static final class Builder {
        private List<MaterialJSON> materialJSONList;

        public Builder withMaterialJSONList(InputStream materialJSONInputStream) {
            try {
                this.materialJSONList = new ObjectMapper().readValue(materialJSONInputStream, MaterialDataSource.class).getData();
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public DataSourceContext build() {
            return new DataSourceContext(materialJSONList);
        }
    }

}
