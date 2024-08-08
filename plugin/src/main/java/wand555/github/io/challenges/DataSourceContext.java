package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Keyed;
import wand555.github.io.challenges.mapping.*;
import wand555.github.io.challenges.mapping.DeathMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public record DataSourceContext(
        List<MaterialJSON> materialJSONList, List<EntityTypeJSON> entityTypeJSONList,
        List<DeathMessage> deathMessageList, List<CraftingTypeJSON> craftingTypeJSONList
) {

    public <T extends DataSourceJSON<K>, K extends Keyed> List<T> getTypedList(T dataSourceElement) {
        MaterialJSON materialJSON = materialJSONList.get(0);
        EntityTypeJSON entityTypeJSON = entityTypeJSONList.get(0);
        if(dataSourceElement.getClass() == materialJSON.getClass()) {
            return (List<T>) materialJSONList;
        }
        if(dataSourceElement.getClass() == entityTypeJSON.getClass()) {
            return (List<T>) entityTypeJSONList;
        } else {
            throw new RuntimeException("'%s' is not a valid class. Valid classes are %s".formatted(dataSourceElement,
                                                                                                   List.of(materialJSON.getClass(),
                                                                                                           entityTypeJSON.getClass()
                                                                                                   )
            ));
        }
    }

    public static final class Builder {
        private List<MaterialJSON> materialJSONList;
        private List<EntityTypeJSON> entityTypeJSONList;
        private List<DeathMessage> deathMessageList;
        private List<CraftingTypeJSON> craftingTypeJSONList;

        public Builder withMaterialJSONList(InputStream materialJSONInputStream) {
            try {
                this.materialJSONList = new ObjectMapper().readValue(materialJSONInputStream,
                                                                     MaterialDataSource.class
                ).getData();
                return this;
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Builder withEntityTypeJSONList(InputStream entityTypeJSONInputStream) {
            try {
                this.entityTypeJSONList = new ObjectMapper().readValue(entityTypeJSONInputStream,
                                                                       EntityTypeDataSource.class
                ).getData();
                return this;
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Builder withDeathMessageList(InputStream deathMessageListInputStream) {
            try {
                this.deathMessageList = new ObjectMapper().readValue(deathMessageListInputStream,
                                                                     DeathMessageDataSource.class
                ).getData();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Builder withCraftingTypeJSONList(InputStream craftingTypeJSONListInputStream) {
            try {
                this.craftingTypeJSONList = new ObjectMapper().readValue(craftingTypeJSONListInputStream,
                                                                     CraftingTypeDataSource.class
                ).getData();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public DataSourceContext build() {
            return new DataSourceContext(materialJSONList, entityTypeJSONList, deathMessageList, craftingTypeJSONList);
        }
    }

}
