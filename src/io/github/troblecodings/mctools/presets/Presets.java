package io.github.troblecodings.mctools.presets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Presets {

	public static final HashMap<String, String[]> PRESET_NAMES = new HashMap<String, String[]>();
	
	static {
		load("itemblock");
	}
	
	public static void load(String name) {
		try {
			String reader = new String(Files.readAllBytes(Paths.get(Presets.class.getResource(name + ".json").toURI())), Charset.forName("utf-8"));
			String[] rg = reader.split("%");
			String[] rt = new String[Math.round(rg.length / (float)2)];
			rt[0] = reader;
			for (int i = 1; i < rt.length; i++) {
				rt[i] = rg[i * 2 - 1];
			}
			PRESET_NAMES.put(name, rt);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
