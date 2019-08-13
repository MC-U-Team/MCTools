package io.github.troblecodings.mctools.presets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Presets {

	public static final HashMap<String, String[]> PRESET_NAMES = new HashMap<String, String[]>();
	public static final HashMap<String, String[]> SILENT_PRESET_NAMES = new HashMap<String, String[]>();

	static {
		loadJ("itemblock");
		loadJ("itemgenerated");
		loadS("modmain");
		loadS("build.gradle");
		loadS("commonproxy");
		loadS("clientproxy");
		loadS("modblocks");
		loadS("moditems");
		loadS("moditemgroups");
		loadS("autogen");
	}
	
	private static void loadJ(String name) {
		try {
			String reader = new String(Files.readAllBytes(Paths.get(Presets.class.getResource(name + ".json").toURI())), Charset.forName("utf-8"));
			PRESET_NAMES.put(name, load(reader));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadS(String name) {
		try {
			String reader = new String(Files.readAllBytes(Paths.get(Presets.class.getResource(name).toURI())), Charset.forName("utf-8"));
			SILENT_PRESET_NAMES.put(name, load(reader));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private static String[] load(String reader) {
		String[] rg = reader.split("%");
		ArrayList<String> arra = new ArrayList<String>();
		for (int i = 1; i < Math.round(rg.length / (float)2); i++) {
			String x = rg[i * 2 - 1];
			if(!arra.contains(x)) 
				arra.add(x);
		}
		String[] rt = new String[arra.size() + 1];
		rt[0] = reader;
		int i = 1;
		for(String x : arra) {
			rt[i++] = x;
		}
		return rt;
	}
	
	public static String getS(final String key, final String... values) {
		String[] pre = Presets.PRESET_NAMES.get(key);
		if(pre == null)
			pre = Presets.SILENT_PRESET_NAMES.get(key);
		String json = pre[0];
		for (int i = 1; i < pre.length; i++) {
			json = json.replaceAll("%" + pre[i] + "%", values[i]);
		}
		return json;
	}
	
	public static String get(final String key, final String... values) {
		String[] pre = Presets.PRESET_NAMES.get(key);
		if(pre == null)
			pre = Presets.SILENT_PRESET_NAMES.get(key);
		String json = pre[0];
		for (int i = 1; i < pre.length; i++) {
			json = json.replaceAll("%" + pre[i] + "%", values[i - 1]);
		}
		return json;
	}
}
