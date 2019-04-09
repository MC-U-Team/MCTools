package io.github.troblecodings.mctools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

public class Settings {

	public static final Path SETTINGS_PATH = Paths.get("settings.json");
	private static JSONObject node;
	
	public static void init() throws Throwable{
		if(!Files.exists(SETTINGS_PATH)) {
			Files.createFile(SETTINGS_PATH);
			node = new JSONObject();
			Files.write(SETTINGS_PATH, "{}".getBytes());
			return;
		}
		
		node = new JSONObject(new String(Files.readAllBytes(SETTINGS_PATH)));
	}
	
	public static String getSetting(StringSetting setting) {
		if(!node.has(setting.name())) return null;
		return node.getString(setting.name());
	}
	
	public static boolean getSetting(BooleanSetting setting) {
		if(!node.has(setting.name())) return false;
		return node.getBoolean(setting.name());
	}
	
	public static void flush() {
		try {
			BufferedWriter writer = Files.newBufferedWriter(SETTINGS_PATH);
			node.write(writer, 3, 0);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setSetting(StringSetting setting, String str) {
		node.put(setting.name(), str);
		flush();
	}
	
	public static void setSetting(BooleanSetting setting, boolean bool) {
		node.put(setting.name(), bool);
		flush();
	}
	
	enum BooleanSetting {
		
	}
	
	enum StringSetting {
		WORK_SPACE
	}
	
}
