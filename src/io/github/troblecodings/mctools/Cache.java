package io.github.troblecodings.mctools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.sun.istack.internal.Nullable;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.jfxtools.ExceptionDialog;

public class Cache {

	private static Path langPath = null;
	private static Path itemPath = null;
	private static Path itemTexturePath = null;

	private static String modid = null;
	private static ArrayList<String> langKeys = null;

	private static HashMap<Path, JSONObject> langJsons = null;
	private static HashMap<Path, JSONObject> itemJsons = null;

	public static String getModID() throws Throwable {
		if (modid == null) {
			Files.readAllLines(Paths.get(Settings.getSetting(StringSetting.WORKSPACE),
					"src\\main\\resources\\META-INF\\mods.toml")).parallelStream().filter(str -> str.contains("modId="))
					.findFirst().ifPresent(str -> modid = str.split("\"")[1]);
		}
		return modid;
	}

	private static HashMap<Path, JSONObject> getJsons(@Nullable HashMap<Path, JSONObject> map, final Path path) throws Throwable {
		if (map == null) {
			final HashMap<Path, JSONObject> map2 = new HashMap<Path, JSONObject>();
			Files.list(path).forEach(pth -> {
				try {
					Reader reader = Files.newBufferedReader(pth, Charset.forName("utf-8"));
					map2.put(pth, new JSONObject(new JSONTokener(reader)));
					reader.close();
				} catch (Throwable e) {
					ExceptionDialog.stacktrace(e);
				}
			});
			return map2;
		}
		return map;
	}
	
/// ITEM SECTION

	public static HashMap<Path, JSONObject> getItemJsons() throws Throwable {
		return itemJsons = getJsons(itemJsons, getItemPath());
	}

/// LANG SECTION

	public static ArrayList<String> getLangKeys() throws Throwable {
		if (langKeys == null) {
			langKeys = new ArrayList<String>();
			getLangJsons();
			langJsons.values().stream().findFirst().ifPresent(obj -> langKeys.addAll(obj.keySet()));
		}
		return langKeys;
	}

	public static HashMap<Path, JSONObject> getLangJsons() throws Throwable {
		return langJsons = getJsons(langJsons, getLangPath());
	}

	public static HashMap<Path, JSONObject> addLangJson(Path name) throws Throwable {
		JSONObject obj = new JSONObject();
		langKeys.forEach(str -> obj.put(str, ""));
		langJsons.put(name, obj);
		return langJsons;
	}

	public static void flushLangJsons() {
		if (langJsons != null) {
			langJsons.forEach((pth, obj) -> {
				try {
					final BufferedWriter writer = Files.newBufferedWriter(pth, Charset.forName("utf-8"));
					obj.write(writer, 1, 1);
					writer.close();
				} catch (IOException e) {
					ExceptionDialog.stacktrace(e);
				}
			});
		}
	}

	public static ArrayList<String> addLangKey(String key) throws Throwable {
		if (langKeys == null) {
			getLangKeys();
		}
		langKeys.add(key);
		langJsons.forEach((pth, obj) -> obj.put(key, ""));
		return langKeys;
	}

/// PATH SECTION

	private static Path getPath(@Nullable Path cache, final String dir) throws Throwable {
		if (cache == null) {
			cache = Paths.get(Settings.getSetting(StringSetting.WORKSPACE),
					"src\\main\\resources\\assets\\" + Cache.getModID() + "\\" + dir);
			if (!Files.exists(cache))
				Files.createDirectories(cache);
		}
		return cache;
	}

	public static Path getLangPath() throws Throwable {
		return langPath = getPath(langPath, "lang");
	}

	public static Path getItemPath() throws Throwable {
		return itemPath = getPath(itemPath, "models\\item");
	}

	public static Path getItemTexturePath() throws Throwable {
		return itemTexturePath = getPath(itemTexturePath, "textures\\item");
	}

/// CLEAR SECTION

	public static void clearLangJsons() {
		langJsons = null;
	}

	public static void clearLangKeys() {
		langKeys = null;
	}

	public static void clearLangPath() {
		langPath = null;
	}

	public static void clearItemPath() {
		itemPath = null;
	}

	public static void clearItemTexturePath() {
		itemTexturePath = null;
	}

	public static void clearModIDCache() {
		modid = null;
	}

	/*
	 * Clears all docs
	 */
	public static void clearCache() {
		clearModIDCache();
		clearLangJsons();
		clearLangKeys();
		clearLangPath();
		clearItemPath();
		clearItemTexturePath();
	}
}
