package org.mockbukkit.metaminer;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface DataGenerator
{
	JsonObject generateData() throws IOException;

	String getFileName();
}
