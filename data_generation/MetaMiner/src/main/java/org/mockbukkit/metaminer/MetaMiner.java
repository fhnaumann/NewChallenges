package org.mockbukkit.metaminer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockbukkit.metaminer.internal.MaterialGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class MetaMiner extends JavaPlugin
{

	public static final String CURRENT_VERSION = "1.21";

	@Override
	public void onEnable()
	{
		this.getLogger().log(Level.INFO, "Generating data for MockBukkit");
		Gson gson = new Gson();
		for (DataGenerator dataGenerator : getDataGenerators())
		{
			try
			{
				JsonObject object = dataGenerator.generateData();
				gson.toJson(object, new FileWriter(new File(getDataFolder(), dataGenerator.getFileName())));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
		this.getLogger().log(Level.INFO, "Successfully generated data!");
		this.getLogger().log(Level.INFO, String.format("The files can be found at '%s'", this.getDataFolder().getPath()));
		this.getLogger().log(Level.INFO, "Copy these files with their respective directories over to the MockBukkit resources folder.");

	}

	private List<DataGenerator> getDataGenerators()
	{
		return List.of(new MaterialGenerator());
	}

}
