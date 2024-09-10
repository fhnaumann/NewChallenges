package org.mockbukkit.metaminer.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.EntityType;
import org.mockbukkit.metaminer.DataGenerator;

import java.io.IOException;
import java.util.Set;

import static org.mockbukkit.metaminer.MetaMiner.CURRENT_VERSION;

public class EntityTypeGenerator implements DataGenerator
{

	@Override
	public JsonObject generateData() throws IOException
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("version", CURRENT_VERSION);
		JsonArray data = new JsonArray();
		for(EntityType entityType : EntityType.values()) {
			if(!entityType.isAlive() || !entityType.isSpawnable() ||  ignore(entityType)) {
				continue;
			}
			JsonObject entry = new JsonObject();
			entry.addProperty("code", entityType.key().value());
			entry.addProperty("translation_key", entityType.translationKey());
			entry.addProperty("img_name", mapImgName(entityType));

			data.add(entry);
		}
		jsonObject.add("data", data);
		return jsonObject;
	}

	private boolean ignore(EntityType entityType) {
		return entityType == EntityType.PLAYER || entityType == EntityType.GIANT;
	}

	private final Set<EntityType> entitiesWithItemNameCollision = Set.of(
			EntityType.CHICKEN,
			EntityType.RABBIT,
			EntityType.COD,
			EntityType.PUFFERFISH,
			EntityType.TROPICAL_FISH
	);

	private String mapImgName(EntityType entityType) {
		String imgName = entityType.key().value();
		if(entitiesWithItemNameCollision.contains(entityType)) {
			imgName += "_entity";
		}
		return imgName + ".png";
	}

	@Override
	public String getFileName()
	{
		return "entity_types";
	}

}
