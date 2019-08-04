package io.github.troblecodings.mctools.lookups;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Lookups {

	public static final ArrayList<String> LANG_LOOKUP = new ArrayList<String>();
	
	static{
		try {
			LANG_LOOKUP.addAll(Files.readAllLines(Paths.get(Lookups.class.getResource("lang").toURI())));
		} catch(Throwable e) {}
	}
	
}
