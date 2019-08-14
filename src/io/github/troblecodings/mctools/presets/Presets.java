package io.github.troblecodings.mctools.presets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Presets {

	public static final HashMap<String, String[]> ITEM_PRESETS = new HashMap<String, String[]>();
	public static final HashMap<String, String[]> BASIC_MOD_CREATION = new HashMap<String, String[]>();

	public static final ArrayList<String> NEEDS_DIALOG = new ArrayList<String>();
	
	static {
		loadS("itemblock", ITEM_PRESETS);
		loadS("itemgenerated", ITEM_PRESETS);
		loadS("modmain_basic", BASIC_MOD_CREATION);
		loadS("buildgradle_basic", BASIC_MOD_CREATION);
		loadS("commonproxy_basic", BASIC_MOD_CREATION);
		loadS("clientproxy_basic", BASIC_MOD_CREATION);
		loadS("modblocks_basic", BASIC_MOD_CREATION);
		loadS("moditems_basic", BASIC_MOD_CREATION);
		loadS("moditemgroups_basic", BASIC_MOD_CREATION);
		loadS("autogen", BASIC_MOD_CREATION);
		loadS("toml_basic", BASIC_MOD_CREATION, true);
		
		loadS("modmain_uteamcore", BASIC_MOD_CREATION);
		loadS("buildgradle_uteamcore", BASIC_MOD_CREATION);
		loadS("commonproxy_uteamcore", BASIC_MOD_CREATION);
		loadS("clientproxy_uteamcore", BASIC_MOD_CREATION);
		loadS("modblocks_uteamcore", BASIC_MOD_CREATION);
		loadS("moditems_uteamcore", BASIC_MOD_CREATION);
		loadS("moditemgroups_uteamcore", BASIC_MOD_CREATION);
		loadS("buildproperties", BASIC_MOD_CREATION, true);
		loadS("toml_uteamcore", BASIC_MOD_CREATION, true);
	}
		
	private static void loadS(String name, HashMap<String, String[]> mp) {
		loadS(name, mp, false);
	}
	
	private static void loadS(String name, HashMap<String, String[]> mp, boolean b) {
		try {
			String reader = new String(Files.readAllBytes(Paths.get(Presets.class.getResource(name).toURI())), Charset.forName("utf-8"));
			mp.put(name, load(reader));
			if(b) {
				NEEDS_DIALOG.add(name);
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private static String[] load(String reader) {
		String[] rg = reader.split("%");
		if(rg.length == 1) {
			return rg;
		}
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
	
	public static String getS(final String key, final HashMap<String, String[]> maps, final String... values) {
		String[] pre = maps.get(key);
		String json = pre[0];
		for (int i = 1; i < pre.length; i++) {
			json = json.replaceAll("%" + pre[i] + "%", values[i]);
		}
		return json;
	}
	
	public static String get(final String key, final HashMap<String, String[]> maps, final String... values) {
		String[] pre = maps.get(key);
		String json = pre[0];
		for (int i = 1; i < pre.length; i++) {
			json = json.replaceAll("%" + pre[i] + "%", values[i - 1] == null ? "":values[i - 1]);
		}
		return json;
	}
}
