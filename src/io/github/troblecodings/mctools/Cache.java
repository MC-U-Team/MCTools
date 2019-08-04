package io.github.troblecodings.mctools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.jfxtools.ExceptionDialog;

public class Cache {

	private static Path langPath = null;
	
	private static String modid = null;
	private static ArrayList<String> langKeys = null;
	private static HashMap<Path, JSONObject> langJsons = null;

	public static String getModID() throws Throwable {
		if (modid == null) {
			Files.readAllLines(Paths.get(Settings.getSetting(StringSetting.WORKSPACE),
					"src\\main\\resources\\META-INF\\mods.toml")).parallelStream().filter(str -> str.contains("modId="))
					.findFirst().ifPresent(str -> modid = str.split("\"")[1]);
		}
		return modid;
	}

	public static void clearModIDCache() {
		modid = null;
	}

	public static ArrayList<String> getLangKeys() throws Throwable {
		if (langKeys == null) {
			langKeys = new ArrayList<String>();
			getLangJsons();
			langJsons.values().stream().findFirst().ifPresent(obj -> langKeys.addAll(obj.keySet()));
		}
		return langKeys;
	}

	public static HashMap<Path, JSONObject> getLangJsons() throws Throwable {
		if (langJsons == null) {
			langJsons = new HashMap<Path, JSONObject>();
			final Path path = getLangPath();
			if (Files.exists(path)) {
				Files.list(path).forEach(pth -> {
					try {
						langJsons.put(pth, new JSONObject(new JSONTokener(Files.newBufferedReader(pth))));
					} catch (Throwable e) {
						ExceptionDialog.stacktrace(e);
					}
				});
			}
		}
		return langJsons;
	}
	
	public static HashMap<Path, JSONObject> addJson(Path name) throws Throwable{
		JSONObject obj = new JSONObject();
		langKeys.forEach(str -> obj.put(str, ""));
		langJsons.put(name, obj);
		return langJsons;
	}
	
	public static void flushLangJsons() {
		if(langJsons != null) {
			langJsons.forEach((pth, obj) -> {
				try {
					final BufferedWriter writer = Files.newBufferedWriter(pth);
					obj.write(writer, 1, 1);
					writer.close();
				} catch (IOException e) {
					ExceptionDialog.stacktrace(e);
				}
			});
		}
	}
	
	public static void clearLangJsons() {
		langJsons = null;
	}

	public static ArrayList<String> addLangKey(String key) throws Throwable {
		if (langKeys == null) {
			getLangKeys();
		}
		langKeys.add(key);
		langJsons.forEach((pth, obj) -> obj.put(key, ""));
		return langKeys;
	}
	
	public static void clearLangKeys() {
		langKeys = null;
	}
	
	public static Path getLangPath() throws Throwable {
		if(langPath == null) {
			langPath =  Paths.get(Settings.getSetting(StringSetting.WORKSPACE),
					"src\\main\\resources\\assets\\" + Cache.getModID() + "\\lang");
			if(!Files.exists(langPath))
				Files.createDirectories(langPath);
		}
		return langPath;
	}

	public static void clearLangPath() {
		langPath = null;
	}
	
	public static void clearCache() {
		clearModIDCache();
		clearLangJsons();
		clearLangKeys();
		clearLangPath();
	}
}
