package io.github.troblecodings.mctools.lookups;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Lookups {

	public static final ArrayList<String> LANG_LOOKUP = new ArrayList<String>();
	public static final JSONObject ITEMMODEL_LOOKUP;

	static{
		load(LANG_LOOKUP, "lang");
		ITEMMODEL_LOOKUP = load("itemmodel.json");
	}
	
	public static JSONObject load(String name) {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(Lookups.class.getResource(name).toURI()), Charset.forName("utf-8"));
			JSONObject obj = new JSONObject(new JSONTokener(reader));
			reader.close();
			return obj;
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void load(ArrayList<String> list, String name) {
		try {
			list.addAll(Files.readAllLines(Paths.get(Lookups.class.getResource(name).toURI())));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
}
