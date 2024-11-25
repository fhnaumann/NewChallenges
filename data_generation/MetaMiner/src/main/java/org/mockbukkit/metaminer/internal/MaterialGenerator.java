package org.mockbukkit.metaminer.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.RedstoneWallTorch;
import org.bukkit.block.data.type.WallHangingSign;
import org.bukkit.block.data.type.WallSign;
import org.mockbukkit.metaminer.DataGenerator;

import java.io.IOException;
import java.util.Set;

import static org.mockbukkit.metaminer.MetaMiner.CURRENT_VERSION;

public class MaterialGenerator implements DataGenerator
{

	@Override
	public JsonObject generateData() throws IOException
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("version", CURRENT_VERSION);
		JsonArray data = new JsonArray();
		for (Material material : Material.values())
		{
			if(ignore(material)) {
				continue;
			}
			JsonObject entry = new JsonObject();
			entry.addProperty("code", material.getKey().value());
			entry.addProperty("is_block", material.isBlock());
			entry.addProperty("is_item", material.isItem());
			entry.addProperty("translation_key", material.translationKey());
			entry.addProperty("img_name", material.getKey().value() + ".png");
			entry.addProperty("weight", asWeight(material));
			data.add(entry);
		}
		jsonObject.add("data", data);
		return jsonObject;
	}

	@Override
	public String getFileName()
	{
		return "materials";
	}

	private int asWeight(Material material) {
		return switch (material.getItemRarity()) {
			case COMMON -> 1;
			case UNCOMMON -> 3;
			case RARE -> 5;
			case EPIC -> 10;
		};
	}

	private static final Set<Material> ignore = Set.of(
			/*
			Unobtainable under any circumstances
			 */
			Material.BARRIER,
			Material.COMMAND_BLOCK,
			Material.COMMAND_BLOCK_MINECART,
			Material.REPEATING_COMMAND_BLOCK,
			Material.CHAIN_COMMAND_BLOCK,
			Material.BUNDLE,
			Material.WATER,
			Material.LAVA,
			Material.END_PORTAL,
			Material.END_GATEWAY,
			Material.NETHER_PORTAL,
			Material.LIGHT,
			Material.STRUCTURE_VOID,
			/*
			Each of the following materials has a non-wall-equivalent which will be used instead.
			 */
			Material.WALL_TORCH,
			Material.REDSTONE_WALL_TORCH,
			Material.SOUL_WALL_TORCH,
			Material.BLACK_WALL_BANNER,
			Material.BLUE_WALL_BANNER,
			Material.BROWN_WALL_BANNER,
			Material.LIGHT_GRAY_WALL_BANNER,
			Material.CYAN_WALL_BANNER,
			Material.GRAY_WALL_BANNER,
			Material.GREEN_WALL_BANNER,
			Material.LIGHT_BLUE_WALL_BANNER,
			Material.LIME_WALL_BANNER,
			Material.MAGENTA_WALL_BANNER,
			Material.ORANGE_WALL_BANNER,
			Material.PINK_WALL_BANNER,
			Material.PURPLE_WALL_BANNER,
			Material.RED_WALL_BANNER,
			Material.WHITE_WALL_BANNER,
			Material.YELLOW_WALL_BANNER
	);

	private boolean ignore(Material material)
	{
		return material.isAir()
				|| material.isEmpty()
				|| material.isLegacy()
				|| ignore.contains(material)
				|| ignoreWallSignHangingWallSign(material.createBlockData());
	}

	private boolean ignoreWallSignHangingWallSign(BlockData blockData) {
		return blockData instanceof WallSign
				|| blockData instanceof HangingSign
				|| blockData instanceof WallHangingSign
				|| blockData instanceof RedstoneWallTorch;
	}

}
